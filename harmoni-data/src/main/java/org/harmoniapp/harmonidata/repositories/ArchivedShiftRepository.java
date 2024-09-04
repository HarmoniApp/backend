package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.ArchivedShifts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedShiftRepository extends JpaRepository<ArchivedShifts, Long> {
}
