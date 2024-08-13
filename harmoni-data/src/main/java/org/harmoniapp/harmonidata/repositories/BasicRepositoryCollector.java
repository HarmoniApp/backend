package org.harmoniapp.harmonidata.repositories;

import lombok.Data;
import org.springframework.stereotype.Repository;


@Repository
@Data
public class BasicRepositoryCollector implements RepositoryCollector {
    private final AbsenceRepository absences;
    private final AbsenceTypeRepository absenceTypes;
    private final AddressRepository addresses;
    private final AvailabilityRepository availabilities;
    private final ContractTypeRepository contractTypes;
    private final LanguageRepository languages;
    private final PredefineShiftRepository predefineShifts;
    private final RoleRepository roles;
    private final ShiftRepository shifts;
    private final StatusRepository statuses;
    private final UserRepository users;
    private final VacationRepository vacations;
}
