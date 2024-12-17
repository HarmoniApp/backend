package org.harmoniapp.harmonidata.repositories;


public interface RepositoryCollector {
    AbsenceRepository getAbsences();

    AbsenceTypeRepository getAbsenceTypes();

    AddressRepository getAddresses();

    ContractTypeRepository getContractTypes();

    GroupRepository getGroups();

    LanguageRepository getLanguages();

    MessageRepository getMessages();

    NotificationRepository getNotifications();

    PredefineShiftRepository getPredefineShifts();

    RoleRepository getRoles();

    ShiftRepository getShifts();

    StatusRepository getStatuses();

    UserRepository getUsers();
}
