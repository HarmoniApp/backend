package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
}
