package org.harmoniapp.repositories.profile;

import org.harmoniapp.entities.profile.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByDepartmentNameNotNull();
}
