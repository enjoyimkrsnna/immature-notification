package com.solus.notification.notification.repository;

import com.solus.notification.notification.model.Notification;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    List<Notification> findByUserIdAndReadFalse(Long userId);

    @Cacheable(value = "notifications", key = "'interval-notifications'", unless = "#result.isEmpty()")
    @Query("SELECT n FROM Notification n WHERE n.triggerType = 'INTERVAL' AND n.isRead = false")
    List<Notification> findIntervalNotifications();


    @Query("SELECT n FROM Notification n WHERE n.triggerType = 'ON_APP_OPEN' AND n.user.id = :userId")
    List<Notification> findAppOpenNotifications(Long userId);
}
