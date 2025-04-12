package org.lakas_vaka.auto_answer.configuration;

import org.lakas_vaka.auto_answer.session.HashMapSessionManager;
import org.lakas_vaka.auto_answer.session.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfiguration {
    @Bean
    public SessionManager sessionManager() {
        return new HashMapSessionManager();
    }
}
