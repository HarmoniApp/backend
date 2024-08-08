package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.AbsenceDto;
import org.harmoniapp.harmoniwebapi.services.AbsenceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/absence")
@CrossOrigin(origins = "http://localhost:3000")
public class AbsenceController {
    private final AbsenceService absenceService;

    @GetMapping
    public List<AbsenceDto> getAllAbsences() {
        return absenceService.getAllAbsences();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceDto createAbsence(@RequestBody AbsenceDto absenceDto) {
        return absenceService.createAbsence(absenceDto);
    }

}

//- [ ] Endpoint który edytuje PUT dla pracownika nie może zmienić statusu ale wszystko inne może
//- [ ] Endpoint dla pracodawcy PUT ale chodzi mi o to ze on będzie aktualizować tylko status np na zaakceptowane czy na odrzucone i jak odrzuci to powinna być informacja zwrotna dlaczego nie może dostać urlopu. PRACODAWCA MOZE TYLKO ZATWIERDZIC LUB ODRZUCIC I TO BEDZIE JAKO PUT NA STATUS TYLKO I WYLACZNIE