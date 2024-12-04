package org.harmoniapp.repositories;

import org.harmoniapp.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderByCreatedAtDesc(long userId);


    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.read = false ORDER BY n.createdAt DESC")
    List<Notification> findAllUnreadByUserIdOrderByCreatedAtDesc(@Param("userId") long userId);
}
