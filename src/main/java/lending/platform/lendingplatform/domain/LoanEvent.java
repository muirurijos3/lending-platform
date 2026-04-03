package lending.platform.lendingplatform.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanEvent {
    private String eventType;
    private Long loanId;
    private BigDecimal amount;
}
