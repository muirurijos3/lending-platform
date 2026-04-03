package lending.platform.lendingplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lending.platform.lendingplatform.domain.*;
import lending.platform.lendingplatform.dto.*;
import lending.platform.lendingplatform.enumeration.InstallmentStatus;
import lending.platform.lendingplatform.enumeration.LoanStatus;
import lending.platform.lendingplatform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanProductRepository loanProductRepository;
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final InstallmentRepository installmentRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final EventPublisher eventPublisher;

    public Loan createLoan(LoanRequestDTO loanRequestDTO){

        Customer customer = customerRepository.findById(loanRequestDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + loanRequestDTO.getCustomerId()));
        LoanProduct loanProduct = loanProductRepository.findById(loanRequestDTO.getProductId())
                .orElseThrow(()-> new EntityNotFoundException("Loan product not found: " + loanRequestDTO.getProductId()));
        if(loanRequestDTO.getAmount().compareTo(customer.getCreditLimit())> 0){
            throw new RuntimeException("Exceeds credit limit");
        }
        /*
        * create a constructor that takes the below params to remove the setting of value
        * */
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setAmount(loanRequestDTO.getAmount());
        loan.setBalance(loanRequestDTO.getAmount());
        loan.setLoanProduct(loanProduct);
        loan.setStatus(LoanStatus.CREATED);


        Loan savedLoan = loanRepository.save(loan);
        eventPublisher.publish(new LoanEvent("LOAN_CREATED", loan.getId(), loan.getAmount()));

        return savedLoan;
    }

    public Loan approveLoan(Long loanId){
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        loan.setStatus(LoanStatus.APPROVED);
        return loanRepository.save(loan);
    }

    public Loan rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        loan.setStatus(LoanStatus.REJECTED);
        return loanRepository.save(loan);
    }

    public Loan repayLoan(Long loanId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        if (loan.getStatus() != LoanStatus.DISBURSED && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Loan is not in a repayable state");
        }

        if(amount.compareTo(loan.getBalance()) > 0){
            throw new RuntimeException("Payment of " + amount + " exceeds outstanding balance of " + loan.getBalance());
        }
        // Find oldest unpaid installment
        Installment installment = installmentRepository.findFirstBySchedule_LoanAndStatusInOrderByDueDateAsc(
                loan, List.of(InstallmentStatus.PENDING, InstallmentStatus.PARTIAL, InstallmentStatus.OVERDUE))
                .orElseThrow(() -> new IllegalStateException("No outstanding installments"));

        BigDecimal newPaid = installment.getAmountPaid().add(amount);
        installment.setAmountPaid(newPaid);

        if (newPaid.compareTo(installment.getTotalDue()) >= 0) {
            installment.setStatus(InstallmentStatus.PAID);
        } else {
            installment.setStatus(InstallmentStatus.PARTIAL);
        }
        installmentRepository.save(installment);

        // Reduce loan balance
        loan.setBalance(loan.getBalance().subtract(amount));
        if (loan.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }
        return loanRepository.save(loan);
    }

    public Loan disburseLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED loans can be disbursed");
        }
        loan.setStatus(LoanStatus.DISBURSED);
        Loan savedLoan = loanRepository.save(loan);
        generateSchedule(savedLoan);
        eventPublisher.publish(new LoanEvent("LOAN_DISBURSED", loan.getId(), savedLoan.getAmount()));
        return savedLoan;

    }

    private RepaymentSchedule generateSchedule(Loan loan) {
        LoanProduct product = loan.getLoanProduct();
        int tenure = product.getTenureMonths();

        // Simple flat-rate monthly interest: monthlyRate = annualRate / 12
        BigDecimal annualRate = product.getInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal principal = loan.getAmount();

        // Standard amortisation formula:
        // M = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(tenure);
        BigDecimal monthly = principal
                .multiply(monthlyRate.multiply(onePlusRPowN))
                .divide(onePlusRPowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        RepaymentSchedule schedule = new RepaymentSchedule();
        schedule.setLoan(loan);
        schedule.setTotalInstallments(tenure);
        schedule.setMonthlyInstallmentAmount(monthly);
        // save schedule first so installments can reference it
        RepaymentSchedule savedSchedule = repaymentScheduleRepository.save(schedule);

        BigDecimal remainingPrincipal = principal;
        LocalDate dueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= tenure; i++) {
            BigDecimal interestForMonth = remainingPrincipal
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalForMonth = monthly.subtract(interestForMonth);

            // Last installment absorbs rounding difference
            if (i == tenure) {
                principalForMonth = remainingPrincipal;
            }

            Installment inst = new Installment();
            inst.setSchedule(savedSchedule);
            inst.setInstallmentNumber(i);
            inst.setDueDate(dueDate);
            inst.setPrincipalAmount(principalForMonth);
            inst.setInterestAmount(interestForMonth);
            inst.setTotalDue(principalForMonth.add(interestForMonth));
            inst.setAmountPaid(BigDecimal.ZERO);
            inst.setStatus(InstallmentStatus.PENDING);

            installmentRepository.save(inst);
            remainingPrincipal = remainingPrincipal.subtract(principalForMonth);
            dueDate = dueDate.plusMonths(1);
        }
        return savedSchedule;
    }


}
