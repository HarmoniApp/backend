package org.harmoniapp.contracts.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.notification.NotificationType;
import org.harmoniapp.entities.user.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Notification.
 *
 * @param id        the unique identifier of the notification
 * @param userId    the ID of the user associated with the notification
 * @param title     the title of the notification
 * @param message   the message content of the notification
 * @param typeName  the type of the notification
 * @param read      indicates if the notification has been read
 * @param createdAt the timestamp when the notification was created
 */
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
        @JsonProperty("type_name") String typeName,

        boolean read,

        @JsonProperty("created_at")LocalDateTime createdAt
        ) {

    /**
     * Converts a Notification entity to a NotificationDto.
     *
     * @param notification the Notification entity to convert
     * @return the resulting NotificationDto
     */
    public static NotificationDto fromEntity(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getUser().getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType().getTypeName(),
                notification.getRead(),
                notification.getCreatedAt()
        );
    }

    /**
     * Converts a NotificationDto to a Notification entity.
     *
     * @param user the user associated with the notification
     * @param type the notification type entity
     * @return the resulting Notification entity
     */
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
