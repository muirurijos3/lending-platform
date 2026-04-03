package lending.platform.lendingplatform.scheduler;

import lending.platform.lendingplatform.domain.*;
import lending.platform.lendingplatform.enumeration.InstallmentStatus;
import lending.platform.lendingplatform.enumeration.LoanStatus;
import lending.platform.lendingplatform.repository.InstallmentRepository;
import lending.platform.lendingplatform.repository.LoanRepository;
import lending.platform.lendingplatform.service.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OverdueInstallmentScheduler {
    /*
    * Preference: would be AWS Lambda, cause of speed
    * */
    private final InstallmentRepository installmentRepository;
    private final LoanRepository loanRepository;
    private final EventPublisher eventPublisher;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void markOverdueInstallments() {
        LocalDate today = LocalDate.now();
        List<Installment> overdue = installmentRepository
                .findByDueDateBeforeAndStatusIn(
                        today,
                        List.of(InstallmentStatus.PENDING, InstallmentStatus.PARTIAL));

        for (Installment inst : overdue) {
            inst.setStatus(InstallmentStatus.OVERDUE);
            installmentRepository.save(inst);

            Loan loan = inst.getSchedule().getLoan();
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);

            eventPublisher.publish(
                    new LoanEvent("LOAN_OVERDUE", loan.getId(), loan.getBalance()));
        }
    }
}