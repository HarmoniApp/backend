package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllBySenderId();
    List<Message> findAllByReceiverId();
}
