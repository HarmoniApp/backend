package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserWithEmpIdDto;
import org.harmoniapp.harmoniwebapi.services.PartialUserService;
import org.harmoniapp.harmoniwebapi.services.PartialUserWithEmpIdService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * Retrieves a list of partial user information, including their employee ID.
     *
     * @return a list of PartialUserWithEmpIdDto objects containing user details
     */
    @GetMapping
    public List<PartialUserWithEmpIdDto> getAllPartialUsers() {
        return partialUserWithEmpIdService.getAllPartialUsers();
    }
}
