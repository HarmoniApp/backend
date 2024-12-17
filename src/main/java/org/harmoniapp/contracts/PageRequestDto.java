package org.harmoniapp.contracts;

import org.springframework.web.bind.annotation.RequestParam;

public record PageRequestDto(@RequestParam(required = false, defaultValue = "1") int pageNumber,
                             @RequestParam(required = false, defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false, defaultValue = "asc") String order) {
}
