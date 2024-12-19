package org.harmoniapp.repositories.chat;

import org.harmoniapp.entities.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Finds all chat partners (both users and groups) for a given user, ordered by the date of the last message.
     *
     * @param userId the ID of the user whose chat partners are to be found
     * @return a list of objects where each object contains the partner ID, partner type (USER or GROUP), and the date of the last message
     */
    @Query(value = """
            SELECT partnerId, partnerType
                FROM (
                    SELECT CASE WHEN m.sender_id = :userId THEN m.receiver_id ELSE m.sender_id END AS partnerId,
                           'USER' AS partnerType,
                           MAX(m.sent_at) AS lastMessageDate
                        FROM message m
                        WHERE (m.sender_id = :userId OR m.receiver_id = :userId)
                        AND m.receiver_id IS NOT NULL
                        GROUP BY partnerId
                        UNION ALL
                    SELECT gm.group_id AS partnerId,
                           'GROUP' AS partnerType,
                           COALESCE(MAX(m.sent_at), TIMESTAMP '2020-01-01 00:00:00') AS lastMessageDate
                        FROM group_members gm
                        LEFT JOIN message m ON m.group_id = gm.group_id
                        WHERE gm.user_id = :userId
                        GROUP BY gm.group_id
                ) AS combined
                ORDER BY lastMessageDate DESC""",
            nativeQuery = true)
    List<Object[]> findAllChatPartners(@Param("userId") Long userId);


    @Query("""
            SELECT m FROM Message m
            WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2)
                OR (m.sender.id = :userId2 AND m.receiver.id = :userId1)
            ORDER BY m.sentAt ASC""")
    List<Message> findChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE m.group.id = :groupId ORDER BY m.sentAt ASC")
    List<Message> findGroupChatHistory(@Param("groupId") Long groupId);

    @Query(value = """
            SELECT m.content FROM Message m
            WHERE (m.sender_id = :userId1 AND m.receiver_id = :userId2)
                OR (m.sender_id = :userId2 AND m.receiver_id = :userId1)
            ORDER BY m.sent_at DESC LIMIT 1""",
            nativeQuery = true)
    String findLastMessageByUsersId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.sender.id = :partnerId AND m.isRead = false")
    List<Message> findUnreadByUsersIds(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

    @Query("SELECT m FROM Message m WHERE m.group.id = :groupId AND m.isRead = false AND m.sender.id != :userId")
    List<Message> findUnreadByGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);


    @Query(value = "SELECT m.content FROM Message m " +
            "WHERE m.group_id = :groupId ORDER BY m.sent_at DESC LIMIT 1", nativeQuery = true)
    String findLastMessageByGroupId(@Param("groupId") Long groupId);

    @Modifying
    @Query(value = "DELETE FROM message WHERE group_id = :groupId", nativeQuery = true)
    void deleteByGroupId(@Param("groupId") Long groupId);
}
