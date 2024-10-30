package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Message;

import java.time.LocalDateTime;

public record MessageDto( //TODO: validation?
        Long id,
        @JsonProperty("sender_id") Long senderId,
        @JsonProperty("receiver_id") Long receiverId,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("sent_at") LocalDateTime sentAt
) {
    public static MessageDto fromEntity(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getSentAt());
    }

    public Message toEntity() {
        return new Message(
                this.id,
                null,
                null,
                this.content,
                this.sentAt != null ? this.sentAt : LocalDateTime.now());
    }

}
