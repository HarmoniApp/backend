package org.harmoniapp.contracts.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.harmoniapp.entities.chat.Message;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for messages.
 *
 * @param id         the unique identifier of the message
 * @param senderId   the ID of the sender
 * @param receiverId the ID of the receiver
 * @param groupId    the ID of the group
 * @param content    the content of the message
 * @param sentAt     the date and time when the message was sent
 * @param isRead     whether the message has been read
 */
@Builder
public record MessageDto(
        Long id,

        @JsonProperty("sender_id")
        @NotNull(message = "Sender ID cannot be null")
        @Positive(message = "Sender ID must be positive")
        Long senderId,

        @JsonProperty("receiver_id")
        @Positive(message = "Receiver ID must be positive")
        Long receiverId,

        @JsonProperty("group_id")
        @Positive(message = "Group ID must be positive")
        Long groupId,

        @NotBlank(message = "Content cannot be blank")
        String content,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("sent_at")
        LocalDateTime sentAt,

        @JsonProperty("is_read")
        boolean isRead) {

    /**
     * Converts a Message entity to a MessageDto.
     *
     * @param message           the Message entity
     * @param translatedContent the translated content of the message
     * @return the corresponding MessageDto
     */
    public static MessageDto fromEntity(Message message, String translatedContent) {
        return MessageDto.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver() != null ? message.getReceiver().getId() : null)
                .groupId(message.getGroup() != null ? message.getGroup().getId() : null)
                .content(translatedContent != null ? translatedContent : message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.isRead())
                .build();
    }

    /**
     * Converts a Message entity to a MessageDto.
     *
     * @param message the Message entity
     * @return the corresponding MessageDto
     */
    public static MessageDto fromEntity(Message message) {
        return fromEntity(message, null);
    }

    /**
     * Converts this MessageDto to a Message entity.
     *
     * @return the corresponding Message entity
     */
    public Message toEntity() {
        return new Message(
                this.id,
                null,
                null,
                null,
                this.content,
                this.sentAt != null ? this.sentAt : LocalDateTime.now(),
                this.isRead
        );
    }
}
