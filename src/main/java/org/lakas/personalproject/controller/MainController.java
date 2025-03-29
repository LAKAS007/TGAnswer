package org.lakas.personalproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.selenium.AuthorizationSelenium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping
public class MainController {


    private final AuthorizationSelenium authorizationSelenium;

    @Autowired
    public MainController(AuthorizationSelenium authorizationSelenium) {
        this.authorizationSelenium = authorizationSelenium;
    }


    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String getLogin(@RequestParam("login") String login) {
        log.info("Login: {}", login);
        authorizationSelenium.startAuthorization(login);
        return "success";
    }
}

