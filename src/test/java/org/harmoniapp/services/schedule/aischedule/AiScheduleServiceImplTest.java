package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.configuration.Principle;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.contracts.schedule.aischedule.AiSchedulerResponseDto;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.services.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AiScheduleServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ScheduleDataEncoder requirementsEncoder;

    @InjectMocks
    private AiScheduleServiceImpl aiScheduleService;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @Mock
    private AggregatedScheduleData data;

    @Mock
    private AlgorithmEntityMapper algorithmEntityMapper;

    @Test
    public void generateScheduleTest() {
        when(authentication.getPrincipal()).thenReturn(new Principle(1L, "username"));
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(user));
        when(requirementsEncoder.prepareData(anyList())).thenReturn(data);
        when(notificationService.create(any())).thenReturn(mock(NotificationDto.class));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), Optional.ofNullable(any()));
        when(algorithmEntityMapper.decodeShifts(anyList(), any())).thenReturn(List.of(mock(Shift.class)));
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
        when(shiftRepository.saveAll(anyList())).thenReturn(List.of(mock(Shift.class)));

        List<ScheduleRequirement> requirementsDto = List.of(mock(ScheduleRequirement.class));

        AiSchedulerResponseDto response = aiScheduleService.generateSchedule(requirementsDto, authentication);

        assertNotNull(response);
    }

    @Test
    public void generateScheduleFailureTest() {
        when(authentication.getPrincipal()).thenReturn(new Principle(1L, "username"));
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);
        when(requirementsEncoder.prepareData(anyList())).thenReturn(data);

        List<ScheduleRequirement> requirementsDto = List.of(mock(ScheduleRequirement.class));
        AiScheduleServiceImpl aiScheduleServiceSpy = spy(aiScheduleService);
        doThrow(new RuntimeException()).when(aiScheduleServiceSpy).runAlgorithm(any(), any());

        AiSchedulerResponseDto response = aiScheduleServiceSpy.generateSchedule(requirementsDto, authentication);

        assertNotNull(response);
        assertFalse(response.success());
    }

    @Test
    public void revokeScheduleTest() {
        Authentication authentication = new TestingAuthenticationToken(new Principle(1L, "username"), null);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(user));
        when(notificationService.create(any())).thenReturn(mock(NotificationDto.class));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), Optional.ofNullable(any()));

        aiScheduleService.generateSchedule(List.of(mock(ScheduleRequirement.class)), authentication);
        AiSchedulerResponseDto response = aiScheduleService.revokeSchedule();

        assertNotNull(response);
        assertNull(response.success());
    }

    @Test
    public void revokeScheduleEmptyTest() {
        AiSchedulerResponseDto response = aiScheduleService.revokeSchedule();

        assertNotNull(response);
    }
}