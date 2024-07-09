package org.harmoniapp.harmonidata.repositories;


public interface RepositoryCollector {
    AbsenceRepository getAbsences();

    AddressRepository getAddresses();

    AvailabilityRepository getAvailabilities();

    PredefineShiftRepository getPredefineShifts();

    ShiftRepository getShifts();

    UserRepository getUsers();

    VacationRepository getVacations();

    UserLanguageRepository getUserLanguages();

    UserRoleRepository getUserRoles();
}
