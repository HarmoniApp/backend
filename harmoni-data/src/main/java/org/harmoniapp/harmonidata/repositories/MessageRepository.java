package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
