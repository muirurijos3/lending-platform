package lending.platform.lendingplatform.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name  = "customer")
@Data
public class Customer extends AuditClass{
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;

    private BigDecimal creditLimit;
}
