package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByUserId(long userId);
}
