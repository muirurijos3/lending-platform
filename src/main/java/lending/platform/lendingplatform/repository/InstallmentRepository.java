package lending.platform.lendingplatform.repository;

import lending.platform.lendingplatform.domain.Installment;
import lending.platform.lendingplatform.enumeration.InstallmentStatus;
import lending.platform.lendingplatform.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    Optional<Installment> findFirstBySchedule_LoanAndStatusInOrderByDueDateAsc(
            Loan loan, List<InstallmentStatus> statuses);

    List<Installment> findByDueDateBeforeAndStatusIn(LocalDate today, List<InstallmentStatus> statuses);
}
