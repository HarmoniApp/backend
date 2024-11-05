package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END AS partnerId " +
            "FROM Message m " +
            "WHERE m.sender.id = :userId OR m.receiver.id = :userId " +
            "GROUP BY partnerId " +
            "ORDER BY MAX(m.sentAt) DESC")
    List<Long> findChatPartners(@Param("userId") Long userId);


    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(value = "SELECT m.content FROM Message m WHERE " +
            "(m.sender_id = :userId1 AND m.receiver_id = :userId2) OR " +
            "(m.sender_id = :userId2 AND m.receiver_id = :userId1) " +
            "ORDER BY m.sent_at DESC LIMIT 1", nativeQuery = true)
    String findLastMessageByUsersId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE " +
            "m.receiver.id = :userId AND m.sender.id = :partnerId AND m.isRead = false")
    List<Message> findUnreadByUsersIds(@Param("userId") Long userId, @Param("partnerId") Long partnerId);
}
