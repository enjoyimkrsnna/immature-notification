package com.solus.notification.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String triggerName;

    @Column(nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private int priority;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = true)
    private TriggerType triggerType;

    @Column(name = "trigger_config", columnDefinition = "TEXT", nullable = true)
    private String triggerConfig;
}
