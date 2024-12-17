package org.harmoniapp.repositories;


import org.harmoniapp.repositories.absence.AbsenceRepository;
import org.harmoniapp.repositories.absence.AbsenceTypeRepository;
import org.harmoniapp.repositories.absence.StatusRepository;
import org.harmoniapp.repositories.chat.GroupRepository;
import org.harmoniapp.repositories.chat.MessageRepository;
import org.harmoniapp.repositories.notification.NotificationRepository;
import org.harmoniapp.repositories.profile.AddressRepository;
import org.harmoniapp.repositories.profile.ContractTypeRepository;
import org.harmoniapp.repositories.profile.LanguageRepository;
import org.harmoniapp.repositories.profile.RoleRepository;
import org.harmoniapp.repositories.schedule.PredefineShiftRepository;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.harmoniapp.repositories.user.UserRepository;

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
