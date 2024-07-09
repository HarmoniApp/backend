package org.harmoniapp.harmonidata.repositories;

import lombok.Data;
import org.springframework.stereotype.Repository;


@Repository
@Data
public class BasicRepositoryCollector implements RepositoryCollector {
    private final AbsenceRepository absences;
    private final AddressRepository addresses;
    private final AvailabilityRepository availabilities;
    private final PredefineShiftRepository predefineShifts;
    private final ShiftRepository shifts;
    private final UserRepository users;
    private final VacationRepository vacations;
    private final UserLanguageRepository userLanguages;
    private final UserRoleRepository userRoles;
}
