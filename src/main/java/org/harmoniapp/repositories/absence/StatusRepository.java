package org.harmoniapp.repositories.absence;

import org.harmoniapp.entities.absence.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
}
