package lending.platform.lendingplatform.service;

import lending.platform.lendingplatform.domain.LoanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name= "kafka.enabled", havingValue = "true")
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, LoanEvent> kafkaTemplate;

    @Override
    public void publish(LoanEvent event) {
        kafkaTemplate.send("loan-event", event);

    }
}
