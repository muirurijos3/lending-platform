package lending.platform.lendingplatform.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message);
    }

}
