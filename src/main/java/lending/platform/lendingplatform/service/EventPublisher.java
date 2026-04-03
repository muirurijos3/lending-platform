package lending.platform.lendingplatform.service;

import lending.platform.lendingplatform.domain.LoanEvent;

public interface EventPublisher {

    void publish(LoanEvent event);
}
