package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRepository extends JpaRepository<Vacation, Long> {
}
