package lending.platform.lendingplatform.domain;

import jakarta.persistence.*;
import lending.platform.lendingplatform.enumeration.InstallmentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "installment")
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private RepaymentSchedule schedule;

    // Which month: 1, 2, 3...
    private Integer installmentNumber;

    private LocalDate dueDate;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    // principalAmount + interestAmount
    private BigDecimal totalDue;

    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status; // PENDING, PAID, PARTIAL, OVERDUE
}