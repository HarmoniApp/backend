package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.harmoniapp.harmonidata.entities.Notification;
import org.harmoniapp.harmonidata.entities.NotificationType;
import org.harmoniapp.harmonidata.entities.User;

import java.time.LocalDateTime;

public record NotificationDto(
        long id,

        @NotNull(message = "User ID cannot be null")
        @Positive(message = "User ID must be a positive number")
        @JsonProperty("user_id") Long userId,

        @NotNull(message = "Title cannot be null")
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title,

        @NotNull(message = "Message cannot be null")
        @Size(min = 1, max = 300, message = "Message must be between 1 and 300 characters")
        String message,

        @NotNull(message = "Type name cannot be null")
        @JsonProperty("type_id") String typeName,

        boolean read,

        @NotNull(message = "Creation date cannot be null")
        @JsonProperty("created_at")LocalDateTime createdAt
        ) {

        public static NotificationDto fromEntity(Notification notification) {
            return new NotificationDto(
                    notification.getId(),
                    notification.getUser().getId(),
                    notification.getTitle(),
                    notification.getMessage(),
                    notification.getType().getTypeName(),
                    notification.isRead(),
                    notification.getCreatedAt()
            );
        }

    public Notification toEntity(User user, NotificationType type) {
        return new Notification(
                this.id,
                user,
                this.title,
                this.message,
                type,
                this.read,
                this.createdAt != null ? this.createdAt : LocalDateTime.now()
        );
    }
}
