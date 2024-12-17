package org.harmoniapp.contracts;

import org.springframework.web.bind.annotation.RequestParam;

public record PageRequestDto(@RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false, defaultValue = "asc") String order) {
}
