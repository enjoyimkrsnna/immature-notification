package com.solus.notification.notification.controller;

import com.solus.notification.notification.dto.CreateNotificationDTO;
import com.solus.notification.notification.model.Notification;
import com.solus.notification.notification.service.NotificationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(
            @Valid @RequestBody CreateNotificationDTO createNotificationDTO,
            @RequestParam(required = false) Long userId) {
        if (createNotificationDTO.getType().equalsIgnoreCase("TRANSACTIONAL")) {
            validate(createNotificationDTO, CreateNotificationDTO.TransactionalGroup.class);
        } else if (createNotificationDTO.getType().equalsIgnoreCase("PROMOTIONAL")) {
            validate(createNotificationDTO, CreateNotificationDTO.PromotionalGroup.class);
        } else {
            throw new IllegalArgumentException("Invalid notification type: " + createNotificationDTO.getType());
        }

        Notification notification = notificationService.createNotification(createNotificationDTO, userId);
        return ResponseEntity.ok(notification);

    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastPromotionalNotification(
            @Valid @RequestBody CreateNotificationDTO createNotificationDTO) {
        notificationService.broadcastPromotionalNotification(createNotificationDTO);
        return ResponseEntity.ok("Promotional notification broadcast to all users.");
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PostMapping("/on-app-open/{userId}")
    public ResponseEntity<String> processAppOpenNotifications(@PathVariable Long userId) {
        notificationService.processAppOpenNotifications(userId);
        return ResponseEntity.ok("App open notifications processed.");
    }

    // Validation Helper Method
    private <T> void validate(T obj, Class<?> group) {
        Set<ConstraintViolation<T>> violations = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(obj, group);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
