package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.PredefineShift;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredefineShiftRepository extends JpaRepository<PredefineShift, Long> {
    List<PredefineShift> findAll(Sort sort);
}
