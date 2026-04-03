package lending.platform.lendingplatform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lending.platform.lendingplatform.domain.Loan;
import lending.platform.lendingplatform.dto.LoanRequestDTO;
import lending.platform.lendingplatform.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan lifecycle operations")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public Loan create(@Valid @RequestBody LoanRequestDTO loanRequestDTO){
        return loanService.createLoan(loanRequestDTO);
    }

    @PostMapping("/{loanId}/approve")
    public Loan approveloan(@PathVariable Long loanId) {
        return loanService.approveLoan(loanId);
    }

    @PostMapping("/{loanId}/disburse")
    public Loan disburse(@PathVariable Long loanId) {
        return loanService.disburseLoan(loanId);
    }

    @PostMapping("/{loanId}/repay")
    public Loan repay(@PathVariable Long loanId,
                      @RequestParam BigDecimal amount) {
        return loanService.repayLoan(loanId, amount);
    }
}
