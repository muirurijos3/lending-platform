package lending.platform.lendingplatform.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "loan_product")
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal maxAmount;
}
