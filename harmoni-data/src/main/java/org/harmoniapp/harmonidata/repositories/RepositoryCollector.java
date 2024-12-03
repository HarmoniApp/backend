package org.harmoniapp.harmonidata.repositories;


public interface RepositoryCollector {
    AbsenceRepository getAbsences();

    AbsenceTypeRepository getAbsenceTypes();

    AddressRepository getAddresses();

    ArchivedShiftRepository getArchivedShifts();

    ContractTypeRepository getContractTypes();

    GroupRepository getGroups();

    LanguageRepository getLanguages();

    MessageRepository getMessages();

    NotificationRepository getNotifications();

    NotificationTypeRepository getNotificationTypes();

    PredefineShiftRepository getPredefineShifts();

    RoleRepository getRoles();

    ShiftRepository getShifts();

    StatusRepository getStatuses();

    UserRepository getUsers();

    VacationRepository getVacations();
}
