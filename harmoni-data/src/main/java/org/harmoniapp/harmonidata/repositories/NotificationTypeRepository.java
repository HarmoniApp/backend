package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    NotificationType findByTypeName(String name);
}
