package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserWithEmpIdDto;
import org.harmoniapp.harmoniwebapi.services.PartialUserWithEmpIdService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user employee ID.
 * Provides endpoints to retrieve user information and their employee ID.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user/simple/empId")
@CrossOrigin(origins = "http://localhost:3000")
public class PartialUserWithEmpIdController {
    private final PartialUserWithEmpIdService partialUserWithEmpIdService;

    /**
     * Retrieves a paginated list of partial user information, including their employee ID.
     *
     * @param pageNumber the page number to retrieve, defaults to 0 if not specified
     * @param pageSize   the number of items per page, defaults to 10 if not specified
     * @return a PageDto containing a list of PartialUserWithEmpIdDto objects with user details
     */
    @GetMapping
    public PageDto<PartialUserWithEmpIdDto> getAllPartialUsers(@RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return partialUserWithEmpIdService.getAllPartialUsers(pageNumber, pageSize);
    }
}
