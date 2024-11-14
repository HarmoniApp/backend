package org.harmoniapp.harmoniwebapi.contracts.AiSchedule;

import java.time.LocalDate;
import java.util.List;

public record ScheduleRequirement(LocalDate date, List<ReqShiftDto> shifts) {
}
