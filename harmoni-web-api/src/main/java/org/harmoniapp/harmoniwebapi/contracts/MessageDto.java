package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Message;

import java.time.LocalDateTime;

public record MessageDto(
        Long id,
        @JsonProperty("sender_id") Long senderId,
        @JsonProperty("receiver_id") Long receiverId,
        String content,
        @JsonProperty("sent_at") LocalDateTime sentAt
) {
    public static MessageDto fromEntity(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getSentAt());
    }

    public Message toEntity() {
        return new Message(
                this.id,
                this.senderId,
                this.receiverId,
                this.content,
                this.sentAt);
    }

}
