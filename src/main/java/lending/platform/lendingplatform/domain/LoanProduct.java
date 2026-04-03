package lending.platform.lendingplatform.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "loan_product")
public class LoanProduct {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal maxAmount;
}
