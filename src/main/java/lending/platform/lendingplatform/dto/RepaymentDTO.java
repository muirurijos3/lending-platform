package lending.platform.lendingplatform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepaymentDTO {
    @NotNull
    private Double amount;
}
