package lending.platform.lendingplatform.service;

import lending.platform.lendingplatform.domain.Customer;
import lending.platform.lendingplatform.domain.Loan;
import lending.platform.lendingplatform.domain.LoanProduct;
import lending.platform.lendingplatform.enumeration.LoanStatus;
import lending.platform.lendingplatform.repository.CustomerRepository;
import lending.platform.lendingplatform.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanService loanService;

    private Customer customer;
    private LoanProduct product;
    private Loan disbursedLoan;


    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setEmail("jane@example.com");
        customer.setCreditLimit(new BigDecimal("30000.00"));

        product = new LoanProduct();
        product.setId(1L);
        product.setName("Personal Loan 12M");
        product.setInterestRate(new BigDecimal("12.00"));
        product.setTenureMonths(12);
        product.setMaxAmount(new BigDecimal("50000.00"));

        disbursedLoan = new Loan();
        disbursedLoan.setId(10L);
        disbursedLoan.setCustomer(customer);
        disbursedLoan.setLoanProduct(product);
        disbursedLoan.setAmount(new BigDecimal("12000.00"));
        disbursedLoan.setBalance(new BigDecimal("12000.00"));
        disbursedLoan.setStatus(LoanStatus.DISBURSED);
    }

    @Test
    @DisplayName("createLoan")
    void createLoan() {
    }

    @Test
    void approveLoan() {
    }

    @Test
    @DisplayName("Test for loan status transition to REJECTED")
    void rejectLoan_existingLoan_statusBecomesRejected() {
        Loan loan = new Loan();
        loan.setId(200L);
        loan.setStatus(LoanStatus.CREATED);

        when(loanRepository.findById(200L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan result = loanService.rejectLoan(200L);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.REJECTED);
    }

    @Test
    void repayLoan() {
    }

    @Test
    void disburseLoan() {
    }
}
