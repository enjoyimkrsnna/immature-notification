package com.solus.notification.notification.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solus.notification.notification.model.TriggerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateNotificationDTO {

    public interface TransactionalGroup {}
    public interface PromotionalGroup {}

    @NotNull(message = "Type is required")
    private String type;

    @NotBlank(message = "Trigger name is required", groups = TransactionalGroup.class)
    private String triggerName;

    @NotBlank(message = "Value is required", groups = TransactionalGroup.class)
    private String value;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Priority is required", groups = PromotionalGroup.class)
    @Positive(message = "Priority must be a positive number", groups = PromotionalGroup.class)
    private Integer priority;

    @NotNull(message = "Trigger type is required", groups = PromotionalGroup.class)
    private TriggerType triggerType;
    @NotNull(message = "triggerConfig is required", groups = PromotionalGroup.class)
    private String triggerConfig;

    public boolean isTriggerConfigValidForInterval() {
        if (triggerType == TriggerType.INTERVAL && triggerConfig != null) {
            try {
                new ObjectMapper().readTree(triggerConfig);
                return true;
            } catch (JsonProcessingException e) {
                return false;
            }
        }
        return true;
    }
}
