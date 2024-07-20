package org.harmoniapp.harmonidata.repositories;


public interface RepositoryCollector {
    AbsenceRepository getAbsences();

    AbsenceTypeRepository getAbsenceTypes();

    AddressRepository getAddresses();

    AvailabilityRepository getAvailabilities();

    ContractTypeRepository getContractTypes();

    LanguageRepository getLanguages();

    PredefineShiftRepository getPredefineShifts();

    RoleRepository getRoles();

    ShiftRepository getShifts();

    UserRepository getUsers();

    VacationRepository getVacations();
}
