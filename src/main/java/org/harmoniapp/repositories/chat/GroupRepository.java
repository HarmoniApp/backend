package org.harmoniapp.repositories.chat;

import org.harmoniapp.entities.chat.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
