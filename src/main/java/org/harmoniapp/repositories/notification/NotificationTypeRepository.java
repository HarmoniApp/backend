package org.harmoniapp.repositories.notification;

import org.harmoniapp.entities.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    NotificationType findByTypeName(String name);
}
