package org.harmoniapp.services.importexport;

import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.entities.schedule.Shift;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleDataService {

    List<ShiftDto> getShifts(LocalDate startDate, LocalDate endDate);
    List<Shift> getShiftsTmp(LocalDate startDate, LocalDate endDate);
}
