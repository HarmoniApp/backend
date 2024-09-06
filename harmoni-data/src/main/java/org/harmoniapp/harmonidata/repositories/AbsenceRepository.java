package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByUserId(long userId);

    List<Absence> findByUserIdAndArchived(long userId, boolean archived);


    @Query("SELECT a FROM Absence a WHERE a.status.id = :statusId")
    List<Absence> findAbsenceByStatusId(@Param("statusId") long statusId);

    @Query("SELECT a FROM Absence a WHERE a.user.id = :userId AND (a.start <= :endDate AND a.end >= :startDate)")
    List<Absence> findAbsenceByDateRangeAndUserId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") long userId);

    @Query("SELECT a FROM Absence a WHERE a.user.id = :userId AND (a.start <= :endDate AND a.end >= :startDate) AND a.status.name = 'approved'")
    List<Absence> findApprovedAbsenceByDateRangeAndUserId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") long userId);

}
