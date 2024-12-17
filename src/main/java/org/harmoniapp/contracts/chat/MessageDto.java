package org.harmoniapp.contracts.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.harmoniapp.entities.chat.Message;

import java.time.LocalDateTime;

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
        boolean isRead
) {
    public static MessageDto fromEntity(Message message, String translatedContent) {
        return new MessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver() != null ? message.getReceiver().getId() : null,
                message.getGroup() != null ? message.getGroup().getId() : null,
                translatedContent != null ? translatedContent : message.getContent(),
                message.getSentAt(),
                message.isRead()
        );
    }

    public static MessageDto fromEntity(Message message) {
        return fromEntity(message, null);
    }

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
