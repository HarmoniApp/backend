package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("SELECT s FROM Shift s WHERE s.start >= :start AND s.end <= :end")
    List<Shift> findAllByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
