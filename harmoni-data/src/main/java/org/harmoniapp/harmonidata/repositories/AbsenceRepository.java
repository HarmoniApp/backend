package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Integer> {
}
