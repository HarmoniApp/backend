package org.harmoniapp.repositories;

import org.harmoniapp.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    NotificationType findByTypeName(String name);
}
