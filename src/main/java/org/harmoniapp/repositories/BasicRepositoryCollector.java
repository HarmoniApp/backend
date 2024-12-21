package org.harmoniapp.repositories;

import lombok.Data;
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
import org.springframework.stereotype.Repository;

/**
 * BasicRepositoryCollector is a repository collector that aggregates various repository interfaces.
 */
@Repository
@Data
public class BasicRepositoryCollector implements RepositoryCollector {
    private final AbsenceRepository absences;
    private final AbsenceTypeRepository absenceTypes;
    private final AddressRepository addresses;
    private final ContractTypeRepository contractTypes;
    private final GroupRepository groups;
    private final LanguageRepository languages;
    private final MessageRepository messages;
    private final NotificationRepository notifications;
    private final PredefineShiftRepository predefineShifts;
    private final RoleRepository roles;
    private final ShiftRepository shifts;
    private final StatusRepository statuses;
    private final UserRepository users;
}
