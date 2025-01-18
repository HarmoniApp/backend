package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.enums.ShiftNotificationType;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.RoleRepository;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShiftServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;
    
    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ShiftNotificationSender shiftNotificationSender;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    @BeforeEach
    public void setUp() {
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
    }
    
    @Test
    public void getByIdTest() {
        long shiftId = 1L;
        User user = User.builder().id(1L).build();
        Shift shift = Shift.builder()
                .id(shiftId)
                .user(user)
                .build();
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        ShiftDto result = shiftService.getById(shiftId);

        assertEquals(shiftId, result.id());
    }

    @Test
    public void getByIdNotFoundTest() {
        long shiftId = 1L;
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shiftService.getById(shiftId));
    }

    @Test
    public void getAllTest() {
        User user = User.builder().id(1L).build();
        Shift shift = Shift.builder().user(user).build();
        List<Shift> shifts = List.of(shift);
        when(shiftRepository.findAll()).thenReturn(shifts);

        List<ShiftDto> result = shiftService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    public void getShiftsByDateRangeAndUserIdTest() {
        String startStr = "2023-01-01T00:00:00";
        String endStr = "2023-01-02T00:00:00";
        long userId = 1L;
        Shift shift = new Shift();
        List<Shift> shifts = List.of(shift);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(shiftRepository.findAllByDateRangeAndUserId(any(LocalDateTime.class), any(LocalDateTime.class), eq(userId))).thenReturn(shifts);

        List<ShiftDto> result = shiftService.getShiftsByDateRangeAndUserId(startStr, endStr, userId);

        assertEquals(1, result.size());
    }

    @Test
    public void createTest() {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .userId(1L)
                .roleName("ROLE")
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .userId(1L)
                .build();
        User user = new User();
        Role role = new Role();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.existsById(shiftDto.userId())).thenReturn(true);
        when(userRepository.findByIdAndIsActiveTrue(shiftDto.userId())).thenReturn(Optional.of(user));
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findByName(shiftDto.roleName())).thenReturn(role);
        when(shiftRepository.save(any(Shift.class))).thenReturn(new Shift());

        ShiftDto result = shiftService.create(shiftDto);

        assertNotNull(result);
    }

    @Test
    public void updateByIdTest() {
        long shiftId = 1L;
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .userId(1L)
                .roleName("ROLE")
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .build();
        User user = new User();
        Role role = new Role();
        Shift shift = shiftDto.toEntity(user,role);
        shift.setId(1L);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.existsById(shiftDto.userId())).thenReturn(true);
        when(userRepository.findByIdAndIsActiveTrue(shiftDto.userId())).thenReturn(Optional.of(user));
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findByName(shiftDto.roleName())).thenReturn(role);
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.save(any(Shift.class))).thenReturn(new Shift());

        ShiftDto result = shiftService.updateById(shiftId, shiftDto);

        assertNotNull(result);
    }

    @Test
    public void publishTest() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 2);
        User user = User.builder().id(1L).build();
        Shift shift = Shift.builder().user(user).build();
        List<Shift> shifts = List.of(shift);
        when(shiftRepository.findAllByDateRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(shifts);
        when(shiftRepository.saveAllAndFlush(anyList())).thenReturn(shifts);

        List<ShiftDto> result = shiftService.publish(start, end);

        assertEquals(1, result.size());
    }

    @Test
    public void deleteByIdTest() {
        long shiftId = 1L;
        Shift shift = Shift.builder().published(true).build();
        shift.setId(shiftId);
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        doNothing().when(shiftRepository).delete(shift);
        doNothing().when(shiftNotificationSender).send(any(Shift.class),any(ShiftNotificationType.class));

        shiftService.deleteById(shiftId);

        verify(shiftRepository, times(1)).delete(shift);
    }
}