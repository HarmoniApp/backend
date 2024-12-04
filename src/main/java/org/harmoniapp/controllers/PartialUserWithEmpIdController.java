package org.harmoniapp.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PartialUserWithEmpIdDto;
import org.harmoniapp.services.PartialUserWithEmpIdService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user employee ID.
 * Provides endpoints to retrieve user information and their employee ID.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/simple/empId")
public class PartialUserWithEmpIdController {
    private final PartialUserWithEmpIdService partialUserWithEmpIdService;

    /**
     * Retrieves a paginated list of partial user information, including their employee ID.
     *
     * @param pageNumber the page number to retrieve (optional, default is 1).
     * @param pageSize   the number of items per page (optional, default is 10).
     * @return a PageDto containing a list of PartialUserWithEmpIdDto objects with user details
     */
    @GetMapping
    public PageDto<PartialUserWithEmpIdDto> getAllPartialUsers(@RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return partialUserWithEmpIdService.getAllPartialUsers(pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public PartialUserWithEmpIdDto getPartialUserById(@PathVariable long id) {
        return partialUserWithEmpIdService.getPartialUserById(id);
    }

    /**
     * Searches for users based on a query string and returns a list of partial user information, including their employee ID.
     *
     * @param q the search query string to filter users by. This can match against various user attributes.
     * @return a list of PartialUserWithEmpIdDto objects that match the search query.
     */
    @GetMapping("/search")
    public List<PartialUserWithEmpIdDto> getUsersSearch(@RequestParam String q) {
        return partialUserWithEmpIdService.getUsersSearch(q);
    }
}
