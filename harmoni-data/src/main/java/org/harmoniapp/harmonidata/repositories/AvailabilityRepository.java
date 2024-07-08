package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
}
