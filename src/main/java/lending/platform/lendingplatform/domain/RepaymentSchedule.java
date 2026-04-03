package lending.platform.lendingplatform.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "repayment_schedule")
public class RepaymentSchedule {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    // Total number of installments = loanProduct.tenureMonths
    private Integer totalInstallments;

    // Snapshot of the monthly amount at schedule creation time
    private BigDecimal monthlyInstallmentAmount;
}
