package lending.platform.lendingplatform.domain;

import jakarta.persistence.*;
import lending.platform.lendingplatform.enumeration.LoanStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "loan")
public class Loan extends AuditClass{
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private LoanProduct loanProduct;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private BigDecimal balance;
}
