package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.geneticalgorithm.Employee;
import org.harmoniapp.geneticalgorithm.Gen;
import org.harmoniapp.geneticalgorithm.Requirements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlgorithmEntityMapperImplTest {

    @Mock
    private AggregatedScheduleData data;

    @Mock
    private PredefineShift predefineShift;

    @Mock
    private User user;

    @Mock
    private Role role;

    @InjectMocks
    private AlgorithmEntityMapperImpl algorithmEntityMapper;

    @Test
    public void decodeShiftsTest() {
        LocalTime now = LocalTime.now();
        List<Requirements> requirements = List.of(new Requirements("1", 1));
        Gen gen = new Gen(1, 1, now, now.plusHours(8), List.of(new Employee("EMP01", "role")), requirements);
        PredefineShift predefineShift = new PredefineShift(1L, "shift_name", now, now.plusHours(8));
        User user = User.builder()
                .employeeId("EMP01")
                .build();
        Role role = new Role(1L, "role", "#000000");

        when(data.predefineShifts()).thenReturn(List.of(predefineShift));
        when(data.users()).thenReturn(List.of(user));
        when(data.roles()).thenReturn(List.of(role));

        List<Shift> result = algorithmEntityMapper.decodeShifts(List.of(gen), data);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, result.get(0).getUser());
        assertEquals(role, result.get(0).getRole());
    }

    @Test
    public void findPredefineShiftTest() {
        when(predefineShift.getId()).thenReturn(1L);
        when(data.predefineShifts()).thenReturn(List.of(predefineShift));

        PredefineShift result = algorithmEntityMapper.findPredefineShift(data.predefineShifts(), 1L);

        assertEquals(predefineShift, result);
    }

    @Test
    public void findPredefineShiftNotFoundTest() {
        when(data.predefineShifts()).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> algorithmEntityMapper.findPredefineShift(data.predefineShifts(), 1L));
    }

    @Test
    public void calculateShiftDateTest() {
        LocalDate now = LocalDate.now();
        int shiftDay = now.getDayOfYear();

        LocalDate result = algorithmEntityMapper.calculateShiftDate(now, shiftDay);

        assertEquals(now, result);
    }

    @Test
    public void calculateShiftEndTest() {
        LocalDate date = LocalDate.now();
        when(predefineShift.getStart()).thenReturn(LocalDateTime.now().toLocalTime());
        when(predefineShift.getEnd()).thenReturn(LocalDateTime.now().toLocalTime().plusHours(8));

        LocalDateTime result = algorithmEntityMapper.calculateShiftEnd(date, predefineShift);

        assertNotNull(result);
    }

    @Test
    public void createDecodedShiftsTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(8);
        Employee employee = mock(Employee.class);
        when(employee.id()).thenReturn("1");
        when(employee.role()).thenReturn("role");
        when(data.users()).thenReturn(List.of(user));
        when(data.roles()).thenReturn(List.of(role));
        when(user.getEmployeeId()).thenReturn("1");
        when(role.getName()).thenReturn("role");

        List<Shift> result = algorithmEntityMapper.createDecodedShifts(start, end, List.of(employee), data);

        assertNotNull(result);
    }

    @Test
    public void createDecodedShiftTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(8);
        Employee employee = mock(Employee.class);
        when(employee.id()).thenReturn("1");
        when(employee.role()).thenReturn("role");
        when(data.users()).thenReturn(List.of(user));
        when(data.roles()).thenReturn(List.of(role));
        when(user.getEmployeeId()).thenReturn("1");
        when(role.getName()).thenReturn("role");

        Shift result = algorithmEntityMapper.createDecodedShift(start, end, employee, data);

        assertNotNull(result);
    }

    @Test
    public void findUserByEmployeeIdTest() {
        when(user.getEmployeeId()).thenReturn("1");
        when(data.users()).thenReturn(List.of(user));

        User result = algorithmEntityMapper.findUserByEmployeeId(data.users(), "1");

        assertEquals(user, result);
    }

    @Test
    public void findUserByEmployeeIdNotFoundTest() {
        when(data.users()).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> algorithmEntityMapper.findUserByEmployeeId(data.users(), "1"));
    }

    @Test
    public void findRoleByNameTest() {
        when(role.getName()).thenReturn("role");
        when(data.roles()).thenReturn(List.of(role));

        Role result = algorithmEntityMapper.findRoleByName(data.roles(), "role");

        assertEquals(role, result);
    }

    @Test
    public void findRoleByNameNotFoundTest() {
        when(data.roles()).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> algorithmEntityMapper.findRoleByName(data.roles(), "role"));
    }
}