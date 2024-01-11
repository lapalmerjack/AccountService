package account.controllers;


import account.entities.responseentities.LoggingResponse;
import account.logging.LoggerEntity;
import account.services.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ROLE_AUDITOR')")
public class SecurityEvents {

        @Autowired
        private LoggerService loggerService;

    @GetMapping("/api/security/events/")
    public ResponseEntity<List<LoggingResponse>> getLoggedEvents() {

        List<LoggingResponse> loggedEvents = loggerService.getLoggedEvents();

        return new ResponseEntity<>(loggedEvents, HttpStatus.OK);


    }
}
