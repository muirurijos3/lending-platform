package lending.platform.lendingplatform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestDTO {
    @NotNull
    private Long customerId;

    @DecimalMin(value = "100.00", message = "Minimum amount for loan is 100")
    private BigDecimal amount;

    @NotNull
    private Long productId;
}
