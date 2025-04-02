package org.lakas.personalproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.model.GlobalContext;
import org.lakas.personalproject.selenium.SeleniumCore;
import org.lakas.personalproject.service.FileLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping
public class MainController {
    private final SeleniumCore seleniumCore;
    private final FileLoggerService loggerService;
    private static boolean isRunning = false;
    private final GlobalContext globalContext;

    @Autowired
    public MainController(SeleniumCore seleniumCore, FileLoggerService loggerService, GlobalContext globalContext) {
        this.seleniumCore = seleniumCore;
        this.loggerService = loggerService;
        this.globalContext = globalContext;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String getLogin(@RequestParam("login") String login) {
        if (globalContext.getCurrentChatContext() != null) {
            String currentLogin = globalContext.getCurrentChatContext().getTelegramLogin();
            if (!login.equals(currentLogin)) {
                seleniumCore.start(login);
            }
        }

        if (isRunning) {
            return "redirect:/status";
        }

        seleniumCore.start(login);
        isRunning = true;
        return "redirect:/status";
    }

    @GetMapping("/status")
    public String getStatus(Model model) {
        List<String> logs = loggerService.getLogs();
        if (logs.isEmpty()) {
            logs.add("Whoops... it seems like you didn't authorize in Telegram");
        }

        model.addAttribute("logsList", logs);

        if (globalContext.getCurrentChatContext() != null) {
            ChatContext chatCtx = globalContext.getCurrentChatContext();
            model.addAttribute("login", chatCtx.getTelegramLogin());
            model.addAttribute("username", chatCtx.getConversatorName());
            model.addAttribute("gender", chatCtx.getConversatorGender());
            model.addAttribute("type", chatCtx.getConversatorType());
        }

        return "success";
    }
}

