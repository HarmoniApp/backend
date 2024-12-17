package org.harmoniapp.repositories.absence;

import org.harmoniapp.entities.absence.Absence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    Page<Absence> findByUserId(long userId, Pageable pageable);

//    Page<Absence> findByUserIdAndArchived(long userId, boolean archived, Pageable pageable);
    @Query("SELECT a FROM Absence a WHERE a.user.isActive = true")
    Page<Absence> findAllWithActiveUsers(Pageable pageable);

    @Query("SELECT a FROM Absence a WHERE a.user.id = :userId AND (a.status.name = 'oczekuje' OR a.status.name = 'zatwierdzony')")
    Page<Absence> findAwaitingOrApprovedAbsenceByUserId(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT a FROM Absence a WHERE a.status.id = :statusId")
    Page<Absence> findAbsenceByStatusId(@Param("statusId") long statusId, Pageable pageable);

    @Query("SELECT a FROM Absence a WHERE a.user.id = :userId AND (a.start <= :endDate AND a.end >= :startDate)")
    List<Absence> findAbsenceByDateRangeAndUserId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") long userId);

    @Query("SELECT a FROM Absence a WHERE a.user.id = :userId AND (a.start <= :endDate AND a.end >= :startDate) AND a.status.name = 'zatwierdzony'")
    List<Absence> findApprovedAbsenceByDateRangeAndUserId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") long userId);

    Boolean existsByUserIdAndId(long userId, long id);
}
