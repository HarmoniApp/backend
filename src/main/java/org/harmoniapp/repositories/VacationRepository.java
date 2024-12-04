package org.harmoniapp.repositories;

import org.harmoniapp.entities.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRepository extends JpaRepository<Vacation, Long> {
}
