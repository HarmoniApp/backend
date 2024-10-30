package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT DISTINCT CASE WHEN m.senderId = :userId THEN m.receiverId ELSE m.senderId END " +
            "FROM Message m " + "WHERE m.senderId = :userId OR m.receiverId = :userId")
    List<Long> findChatPartners(Long userId);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findChatHistory(Long userId1, Long userId2);
}
