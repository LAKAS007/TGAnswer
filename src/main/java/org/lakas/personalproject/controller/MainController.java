package org.lakas.personalproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping
public class MainController {
    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String getLogin(@RequestParam("login") String login) {
        log.info("Login: {}", login);
        // AuthenticationSelenium.startAuthorization(login);
        return "success";
    }
}

