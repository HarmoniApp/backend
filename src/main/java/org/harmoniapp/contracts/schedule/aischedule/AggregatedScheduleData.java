package org.harmoniapp.contracts.schedule.aischedule;

import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.geneticalgorithm.Employee;
import org.harmoniapp.geneticalgorithm.Gen;

import java.util.List;
import java.util.Map;

public record AggregatedScheduleData(List<User> users,
                                     List<PredefineShift> predefineShifts,
                                     List<Role> roles,
                                     Map<String, List<Employee>> employees,
                                     List<Gen> shifts) {
}
