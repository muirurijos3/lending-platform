package lending.platform.lendingplatform.service;

import lending.platform.lendingplatform.domain.LoanEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class LoanEventConsumer {
    Logger log = LoggerFactory.getLogger(LoanEventConsumer.class);
    @Autowired
    NotificationService notificationService;

    public void consume(LoanEvent event){
        log.info("Consumed event:" + event);
        log.info("Consumed event: {}", event);
        switch (event.getEventType()) {
            case "LOAN_CREATED"    -> notificationService.sendNotification("Loan " + event.getLoanId() + " created");
            case "LOAN_DISBURSED"  -> notificationService.sendNotification("Funds disbursed for loan " + event.getLoanId());
            case "LOAN_OVERDUE"    -> notificationService.sendNotification("Loan " + event.getLoanId() + " is overdue");
        }
    }
}
