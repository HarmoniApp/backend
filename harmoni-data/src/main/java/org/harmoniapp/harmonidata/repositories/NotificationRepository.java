package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserId(long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.read = false")
    List<Notification> findAllUnreadByUserId(@Param("userId") long userId);
}
