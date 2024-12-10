package org.harmoniapp.repositories.absence;

import org.harmoniapp.entities.absence.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRepository extends JpaRepository<Vacation, Long> {
}
