package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
