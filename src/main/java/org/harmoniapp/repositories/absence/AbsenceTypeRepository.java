package org.harmoniapp.repositories.absence;

import org.harmoniapp.entities.absence.AbsenceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceTypeRepository extends JpaRepository<AbsenceType, Long> {
}
