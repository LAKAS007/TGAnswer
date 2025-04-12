package org.lakas_vaka.auto_answer.controller.rest;

import org.lakas_vaka.auto_answer.service.rest.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionRestEntity>> getSessions() {
        return ResponseEntity.of(Optional.ofNullable(sessionService.getSessions()));
    }
}
