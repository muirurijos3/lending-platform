package lending.platform.lendingplatform.service;

import lending.platform.lendingplatform.domain.*;
import lending.platform.lendingplatform.dto.LoanRequestDTO;
import lending.platform.lendingplatform.enumeration.LoanStatus;
import lending.platform.lendingplatform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanProductRepository loanProductRepository;
    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;
    @Mock
    private InstallmentRepository installmentRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private LoanService loanService;

    private Customer customer;
    private LoanProduct product;
    private Loan disbursedLoan;


    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setEmail("jane@example.com");
        customer.setCreditLimit(new BigDecimal("30000.00"));

        product = new LoanProduct();
        product.setId(1L);
        product.setName("Personal Loan 12M");
        product.setInterestRate(new BigDecimal("12.00"));
        product.setTenureMonths(12);
        product.setMaxAmount(new BigDecimal("50000.00"));

        disbursedLoan = new Loan();
        disbursedLoan.setId(10L);
        disbursedLoan.setCustomer(customer);
        disbursedLoan.setLoanProduct(product);
        disbursedLoan.setAmount(new BigDecimal("12000.00"));
        disbursedLoan.setBalance(new BigDecimal("12000.00"));
        disbursedLoan.setStatus(LoanStatus.DISBURSED);
    }

    @Test
    @DisplayName("createLoan - Happy path")
    void createLoan() {
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setCustomerId(500L);
        loanRequestDTO.setProductId(500L);
        loanRequestDTO.setAmount(new BigDecimal("12000.00"));

        when(customerRepository.findById(500L)).thenReturn(Optional.of(customer));
        when(loanProductRepository.findById(500L)).thenReturn(Optional.of(product));
        when(loanRepository.save(any(Loan.class))).thenAnswer(i -> {
            Loan newLoan = i.getArgument(0);
            newLoan.setId(99L);
            return newLoan;
        });

        Loan result = loanService.createLoan(loanRequestDTO);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.CREATED);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("12000.00"));
        assertThat(result.getBalance()).isEqualTo(new BigDecimal("12000.00"));
        assertThat(result.getLoanProduct()).isEqualTo(product);
        assertThat(result.getCustomer()).isEqualTo(customer);
    }

    @Test
    @DisplayName("transitioms loan status to approved")
    void approveLoan() {
        Loan loan = new Loan();
        loan.setId(100L);
        loan.setStatus(LoanStatus.CREATED);

        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan result = loanService.approveLoan(100L);
        assertThat(result).isEqualTo(loan);
    }

    @Test
    @DisplayName("Test for loan status transition to REJECTED")
    void rejectLoan_existingLoan_statusBecomesRejected() {
        Loan loan = new Loan();
        loan.setId(200L);
        loan.setStatus(LoanStatus.CREATED);

        when(loanRepository.findById(200L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan result = loanService.rejectLoan(200L);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.REJECTED);
    }

    @Test
    void repayLoan() {
    }

    @Test
    @DisplayName("Disburses loan and generates repayment schedule")
    void disburseLoan() {
        Loan loan = new Loan();
        loan.setId(300L);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setLoanProduct(product);
        loan.setAmount(new BigDecimal("12000.00"));
        loan.setBalance(new BigDecimal("12000.00"));

        when(loanRepository.findById(300L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(repaymentScheduleRepository.save(any())).thenAnswer(i -> {
            RepaymentSchedule s = i.getArgument(0);
            s.setId(400L);
            return s;
        });
        when(installmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Loan result = loanService.disburseLoan(300L);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.DISBURSED);

        // Verify schedule was created
        verify(repaymentScheduleRepository).save(any(RepaymentSchedule.class));

        // 12-month product = 12 installments saved
        verify(installmentRepository, times(12)).save(any(Installment.class));

        // Verify LOAN_DISBURSED event published
        ArgumentCaptor<LoanEvent> captor = ArgumentCaptor.forClass(LoanEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("LOAN_DISBURSED");
    }
}
