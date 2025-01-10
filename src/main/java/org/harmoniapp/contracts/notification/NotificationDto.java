package org.harmoniapp.contracts.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.user.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Notification.
 *
 * @param id        the unique identifier of the notification
 * @param userId    the ID of the user associated with the notification
 * @param title     the title of the notification
 * @param message   the message content of the notification
 * @param read      indicates if the notification has been read
 * @param createdAt the timestamp when the notification was created
 */
@Builder
public record NotificationDto(
        Long id,

        @NotNull(message = "ID użytkownika nie może być puste")
        @Positive(message = "ID użytkownika musi być liczbą dodatnią")
        @JsonProperty("user_id") Long userId,

        @NotNull(message = "Tytuł nie może być pusty")
        @Size(min = 1, max = 100, message = "Tytuł musi zawierać od 1 do 100 znaków")
        String title,

        @NotNull(message = "Wiadomość nie może być pusta")
        @Size(min = 1, max = 300, message = "Wiadomość musi zawierać od 1 do 300 znaków")
        String message,

        boolean read,

        @JsonProperty("created_at") LocalDateTime createdAt
) {

    public static NotificationDto createNotification(long receiverId, String title, String message) {
        return NotificationDto.builder()
                .userId(receiverId)
                .title(title)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Converts a Notification entity to a NotificationDto.
     *
     * @param notification the Notification entity to convert
     * @return the resulting NotificationDto
     */
    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    /**
     * Converts a NotificationDto to a Notification entity.
     *
     * @param user the user associated with the notification
     * @return the resulting Notification entity
     */
    public Notification toEntity(User user) {
        return Notification.builder()
                .id(this.id)
                .user(user)
                .title(this.title)
                .message(this.message)
                .read(this.read)
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                .build();
    }
}
