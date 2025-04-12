package org.lakas_vaka.auto_answer.service.rest;

import org.lakas_vaka.auto_answer.controller.rest.SessionRestEntity;
import org.lakas_vaka.auto_answer.session.SessionManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {
    private final SessionManager sessionManager;

    public SessionService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public List<SessionRestEntity> getSessions() {
        return sessionManager.getAll().stream()
                .map(x -> SessionRestEntity.builder()
                            .login(x.getLogin())
                            .hasDriver(x.getWebDriver() != null)
                            .conversatorContext(x.getConversatorContext())
                            .messagesSize(x.getMessages().size())
                            .isEnabled(x.isEnabled())
                            .build())
                .toList();
    }
}
