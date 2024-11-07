package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Message;

import java.time.LocalDateTime;

public record MessageDto( //TODO: validation?
        Long id,
        @JsonProperty("sender_id") Long senderId,
        @JsonProperty("receiver_id") Long receiverId,
        @JsonProperty("group_id") Long groupId,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("sent_at") LocalDateTime sentAt,
        @JsonProperty("is_read") boolean isRead
) {
    public static MessageDto fromEntity(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver() != null ? message.getReceiver().getId() : null,
                message.getGroup() != null ? message.getGroup().getId() : null,
                message.getContent(),
                message.getSentAt(),
                message.isRead()
        );
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
