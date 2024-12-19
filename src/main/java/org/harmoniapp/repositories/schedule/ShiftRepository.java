package org.harmoniapp.repositories.schedule;

import org.harmoniapp.entities.schedule.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("SELECT s FROM Shift s WHERE (s.start <= :end AND s.end >= :start) AND s.user.id = :userId")
    List<Shift> findAllByDateRangeAndUserId(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end, @Param("userId") Long userId);

    @Query("SELECT s FROM Shift s WHERE s.start BETWEEN :start AND :end")
    List<Shift> findAllByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT s FROM Shift s
            WHERE s.published = TRUE and s.user.isActive = TRUE AND s.start BETWEEN :start AND :end""")
    List<Shift> findPublishedByDataRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Shift s WHERE (s.start <= :end AND s.end >= :start) AND s.user.id = :userId AND s.published = true")
    List<Shift> findPublishedByDateRangeAndUserId(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end, @Param("userId") Long userId);

    List<Shift> findByRole_Id(Long id);
}
