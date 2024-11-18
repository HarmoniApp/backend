package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END AS partnerId " +
            "FROM Message m " +
            "WHERE (m.sender.id = :userId OR m.receiver.id = :userId) " +
            "AND m.receiver.id IS NOT NULL " +
            "GROUP BY partnerId " +
            "ORDER BY MAX(m.sentAt) DESC")
    List<Long> findChatPartners(@Param("userId") Long userId);

    @Query(value = "SELECT gm.group_id " +
            "FROM group_members gm " +
            "LEFT JOIN message m ON m.group_id = gm.group_id " +
            "WHERE gm.user_id = :userId " +
            "GROUP BY gm.group_id " +
            "ORDER BY COALESCE(MAX(m.sent_at), TIMESTAMP '2020-01-01 00:00:00') DESC", nativeQuery = true)
    List<Long> findGroupChatPartners(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1)" +
            "ORDER BY m.sentAt ASC")
    List<Message> findChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE m.group.id = :groupId ORDER BY m.sentAt ASC")
    List<Message> findGroupChatHistory(@Param("groupId") Long groupId);

    @Query(value = "SELECT m.content FROM Message m WHERE " +
            "(m.sender_id = :userId1 AND m.receiver_id = :userId2) OR " +
            "(m.sender_id = :userId2 AND m.receiver_id = :userId1) " +
            "ORDER BY m.sent_at DESC LIMIT 1", nativeQuery = true)
    String findLastMessageByUsersId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE " +
            "m.receiver.id = :userId AND m.sender.id = :partnerId AND m.isRead = false")
    List<Message> findUnreadByUsersIds(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

    @Query("SELECT m FROM Message m WHERE " +
            "m.group.id = :groupId AND m.isRead = false AND m.sender.id != :userId")
    List<Message> findUnreadByGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);


    @Query(value = "SELECT m.content FROM Message m " +
            "WHERE m.group_id = :groupId " +
            "ORDER BY m.sent_at DESC LIMIT 1", nativeQuery = true)
    String findLastMessageByGroupId(@Param("groupId") Long groupId);

    @Modifying
    @Query(value = "DELETE FROM message WHERE group_id = :groupId", nativeQuery = true)
    void deleteByGroupId(@Param("groupId") Long groupId);
}
