package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Absence;
import org.harmoniapp.harmonidata.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByUserId(long userId);
    @Query("SELECT a FROM Absence a WHERE a.status.id = :statusId")
    List<Absence> findAbsenceByStatusId(@Param("statusId") long statusId);}
