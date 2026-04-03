package lending.platform.lendingplatform.service;

import lending.platform.lendingplatform.domain.LoanEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "false", matchIfMissing = true)
public class LocalEventPublisher implements EventPublisher {

    @Override
    public void publish(LoanEvent event) {
        System.out.println("Event " + event);
    }
}
