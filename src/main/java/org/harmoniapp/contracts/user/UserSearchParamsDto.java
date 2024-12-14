package org.harmoniapp.contracts.user;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public record UserSearchParamsDto(
        @RequestParam(required = false) List<Long> roles,
        @RequestParam(required = false) List<Long> contracts,
        @RequestParam(required = false) List<Long> language){
}
