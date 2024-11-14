package org.harmoniapp.harmoniwebapi.contracts.AiSchedule;

import java.util.List;

public record ReqShiftDto(Long shiftId, List<ReqRoleDto> roles) {
}
