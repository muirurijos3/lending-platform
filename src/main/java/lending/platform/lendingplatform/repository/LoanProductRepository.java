package lending.platform.lendingplatform.repository;

import lending.platform.lendingplatform.domain.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
}
