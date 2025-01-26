package com.solus.notification.notification.service;

import com.solus.notification.notification.dto.CreateNotificationDTO;
import com.solus.notification.notification.model.Notification;
import com.solus.notification.notification.model.NotificationType;
import com.solus.notification.notification.model.TriggerType;
import com.solus.notification.notification.model.User;
import com.solus.notification.notification.repository.NotificationRepository;
import com.solus.notification.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Value("${notification.processing.batch-size:500}")
    private int batchSize;

    @Autowired
    private TaskScheduler taskScheduler;

    public Notification createNotification(CreateNotificationDTO dto, Long userId) {
        NotificationType type = NotificationType.valueOf(dto.getType());
        if (type == NotificationType.TRANSACTIONAL) {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required for transactional notifications.");
            }
        }
        Notification notification = Notification.builder()
                .message(dto.getMessage())
                .triggerName(dto.getTriggerName())
                .value(dto.getValue())
                .type(type)
                .priority(type == NotificationType.TRANSACTIONAL ? 0 : dto.getPriority())
                .triggerType(type == NotificationType.PROMOTIONAL ? dto.getTriggerType() : null)
                .triggerConfig(type == NotificationType.PROMOTIONAL ? dto.getTriggerConfig() : null)
                .isRead(false)
                .user(userId != null ? userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")) : null)
                .build();

        return notificationRepository.save(notification);
    }

    public void broadcastPromotionalNotification(CreateNotificationDTO dto) {
        int page = 0;
        List<User> users;

        do {
            users = userRepository.findAll(PageRequest.of(page, batchSize)).getContent();
            List<Notification> notifications = users.stream().map(user -> Notification.builder()
                    .user(user)
                    .type(NotificationType.PROMOTIONAL)
                    .message(dto.getMessage())
                    .priority(dto.getPriority())
                    .triggerName("Broadcast")
                    .value("Promotional")
                    .isRead(false)
                    .triggerType(dto.getTriggerType())
                    .triggerConfig(dto.getTriggerConfig())
                    .build()).toList();

            notificationRepository.saveAll(notifications);
            page++;
        } while (!users.isEmpty());
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

//    @Scheduled(cron = "${notification.interval.cron:0 */1 * * * *}")
//    public void processIntervalNotifications() {
//        List<Notification> intervalNotifications = notificationRepository.findIntervalNotifications();
//
//        for (Notification notification : intervalNotifications) {
//            sendNotification(notification);
//
//            if (notification.getTriggerType() == TriggerType.ONE_TIME) {
//                notificationRepository.delete(notification);
//            }
//        }
//    }

    @Scheduled(cron = "*/5 * * * * *")  // Runs every 5 seconds to check notifications
    public void processIntervalNotifications() {
        List<Notification> intervalNotifications = notificationRepository.findIntervalNotifications();
        System.out.println(Arrays.toString(intervalNotifications.toArray()) + " size "+ intervalNotifications.size());

        for (Notification notification : intervalNotifications) {
            long interval = Long.parseLong(notification.getTriggerConfig());
            Instant startTime = Instant.now().plusMillis(interval);
            taskScheduler.schedule(() -> {
                sendNotification(notification);
            }, startTime);
        }
    }




    public void processAppOpenNotifications(Long userId) {
        List<Notification> appOpenNotifications = notificationRepository.findAppOpenNotifications(userId);

        for (Notification notification : appOpenNotifications) {
            sendNotification(notification);
        }
    }

    private void sendNotification(Notification notification) {
        System.out.println("Sending notification to user " +
                (notification.getUser() != null ? notification.getUser().getId() : "all users") +
                ": " + notification.getMessage());
    }
}
