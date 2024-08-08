package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
}
