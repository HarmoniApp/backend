// ScheduleDataEncoderImplTest.java
package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.contracts.schedule.aischedule.ReqRoleDto;
import org.harmoniapp.contracts.schedule.aischedule.ReqShiftDto;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.InvalidAiScheduleRequirementsException;
import org.harmoniapp.geneticalgorithm.Employee;
import org.harmoniapp.geneticalgorithm.Requirements;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.RoleRepository;
import org.harmoniapp.repositories.schedule.PredefineShiftRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleDataEncoderImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PredefineShiftRepository predefineShiftRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private ScheduleDataEncoderImpl scheduleDataEncoder;

    @Test
    public void prepareDataTest() {
        List<ScheduleRequirement> requirementsDto = new ArrayList<>();
        requirementsDto.add(mock(ScheduleRequirement.class));
        List<User> users = List.of(mock(User.class));
        List<Role> roles = List.of(mock(Role.class));
        List<PredefineShift> predefineShifts = List.of(mock(PredefineShift.class));

        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findAll()).thenReturn(roles);
        when(repositoryCollector.getPredefineShifts()).thenReturn(predefineShiftRepository);
        when(predefineShiftRepository.findAll()).thenReturn(predefineShifts);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllActiveWithoutAbsenceInDateRange(any(), any())).thenReturn(users);

        AggregatedScheduleData result = scheduleDataEncoder.prepareData(requirementsDto);

        assertNotNull(result);
    }

    @Test
    public void validAndSortRequirementsTest() {
        List<ScheduleRequirement> requirementsDto = new ArrayList<>(List.of(
                new ScheduleRequirement(LocalDate.of(2023, 1, 1), List.of()),
                new ScheduleRequirement(LocalDate.of(2023, 1, 2), List.of())
        ));

        assertDoesNotThrow(() -> scheduleDataEncoder.validAndSortRequirements(requirementsDto));
    }

    @Test
    public void validAndSortRequirementsThrowsExceptionTest() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<ScheduleRequirement> requirementsDto = new ArrayList<>(List.of(
                new ScheduleRequirement(date, List.of()),
                new ScheduleRequirement(date, List.of())
        ));

        InvalidAiScheduleRequirementsException exception = assertThrows(
                InvalidAiScheduleRequirementsException.class,
                () -> scheduleDataEncoder.validAndSortRequirements(requirementsDto)
        );

        assertEquals("Data %s została podana wiele razy".formatted(date), exception.getMessage());
    }

    @Test
    public void findActiveUsersWithoutAbsenceTest() {
        List<ScheduleRequirement> requirementsDto = List.of(mock(ScheduleRequirement.class));
        List<User> users = List.of(new User());
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllActiveWithoutAbsenceInDateRange(any(), any())).thenReturn(users);

        List<User> result = scheduleDataEncoder.findActiveUsersWithoutAbsence(requirementsDto);

        assertEquals(users, result);
    }

    @Test
    public void prepareEmployeesTest() {
        List<ScheduleRequirement> requirements = List.of(mock(ScheduleRequirement.class));
        List<User> users = List.of(mock(User.class));
        Map<String, List<Employee>> result = scheduleDataEncoder.prepareEmployees(requirements, users);

        assertNotNull(result);
    }

    @Test
    public void getValidRolesTest() {
        List<ScheduleRequirement> requirements = List.of(mock(ScheduleRequirement.class));
        assertDoesNotThrow(() -> scheduleDataEncoder.getValidRoles(requirements));
    }

    @Test
    public void getUniqueEmployeesTest() {
        List<User> users = List.of(mock(User.class));
        Set<Long> validRoles = Set.of(1L);
        List<Employee> result = scheduleDataEncoder.getUniqueEmployees(users, validRoles);

        assertNotNull(result);
    }

    @Test
    public void verifyUserQuantityTest() {
        List<ScheduleRequirement> requirementsDto = List.of(mock(ScheduleRequirement.class));
        Map<String, List<Employee>> employees = Map.of("role", List.of(mock(Employee.class)));
        List<Role> roles = List.of(mock(Role.class));

        assertDoesNotThrow(() -> scheduleDataEncoder.verifyUserQuantity(requirementsDto, employees, roles));
    }

    @Test
    public void summarizeRequiredEmployeesTest() {
        List<ScheduleRequirement> requirementsDto = List.of(mock(ScheduleRequirement.class));
        List<Role> roles = List.of(mock(Role.class));
        assertDoesNotThrow(() -> scheduleDataEncoder.summarizeRequiredEmployees(requirementsDto, roles));
    }

    @Test
    public void findRoleNameByIdTest() {
        List<Role> roles = List.of(mock(Role.class));
        Long roleId = 1L;
        when(roles.get(0).getId()).thenReturn(roleId);
        when(roles.get(0).getName()).thenReturn("roleName");

        String result = scheduleDataEncoder.findRoleNameById(roles, roleId);

        assertEquals("roleName", result);
    }

    @Test
    public void calculateAvailableEmployeesTest() {
        List<ScheduleRequirement> requirementsDto = List.of(mock(ScheduleRequirement.class));
        Map<String, List<Employee>> employees = Map.of("role", List.of(mock(Employee.class)));

        assertDoesNotThrow(() -> scheduleDataEncoder.calculateAvailableEmployees(requirementsDto, employees));
    }

    @Test
    public void checkEmployeeAvailabilityTest() {
        Map<String, Integer> required = Map.of("role", 1);
        Map<String, Integer> available = Map.of("role", 1);

        assertDoesNotThrow(() -> scheduleDataEncoder.checkEmployeeAvailability(required, available));
    }

    @Test
    public void prepareShiftsTest() {
        List<ScheduleRequirement> scheduleRequirements = List.of(mock(ScheduleRequirement.class));
        List<PredefineShift> predefineShifts = List.of(mock(PredefineShift.class));
        List<Role> roles = List.of(mock(Role.class));

        assertDoesNotThrow(() -> scheduleDataEncoder.prepareShifts(scheduleRequirements, predefineShifts, roles));
    }

    @Test
    public void sortShiftsByStartTest() {
        ScheduleRequirement scheduleRequirement = mock(ScheduleRequirement.class);
        List<PredefineShift> predefineShifts = List.of(mock(PredefineShift.class));

        assertDoesNotThrow(() -> scheduleDataEncoder.sortShiftsByStart(scheduleRequirement, predefineShifts));
    }

    @Test
    public void prepareRequirementsTest() {
        List<ReqRoleDto> requirements = List.of(mock(ReqRoleDto.class));
        List<Role> roles = List.of(mock(Role.class));

        assertDoesNotThrow(() -> scheduleDataEncoder.prepareRequirements(requirements, roles));
    }

    @Test
    public void createGenTest() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        ReqRoleDto reqRoleDto = new ReqRoleDto(1L, 1);
        ReqShiftDto reqShiftDto = new ReqShiftDto(1L, List.of(reqRoleDto));
        List<ReqShiftDto> shifts = List.of(reqShiftDto);
        ScheduleRequirement scheduleRequirement = new ScheduleRequirement(futureDate, shifts);

        List<PredefineShift> predefineShifts = List.of(PredefineShift.builder().id(1L).build());
        List<Requirements> requirements = List.of(mock(Requirements.class));

        assertDoesNotThrow(() -> scheduleDataEncoder.createGen(reqShiftDto, scheduleRequirement, predefineShifts, requirements));
    }

    @Test
    public void findShiftStartTimeTest() {
        List<PredefineShift> predefineShifts = List.of(mock(PredefineShift.class));
        ReqShiftDto reqShiftDto = mock(ReqShiftDto.class);

        assertDoesNotThrow(() -> scheduleDataEncoder.findShiftStartTime(predefineShifts, reqShiftDto));
    }
}