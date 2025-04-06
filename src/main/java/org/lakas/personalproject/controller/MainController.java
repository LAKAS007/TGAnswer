package org.lakas.personalproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.model.GlobalContext;
import org.lakas.personalproject.selenium.SeleniumCore;
import org.lakas.personalproject.service.FileLoggerService;
import org.lakas.personalproject.service.SeleniumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping
public class MainController {
    private final FileLoggerService loggerService;
    private final GlobalContext globalContext;
    private final SeleniumService seleniumService;

    @Autowired
    public MainController(FileLoggerService loggerService, GlobalContext globalContext, SeleniumService seleniumService) {
        this.loggerService = loggerService;
        this.globalContext = globalContext;
        this.seleniumService = seleniumService;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("login") String login) {
        seleniumService.startReplyingTo(login);
        return "redirect:/status?login=" + login;
    }

    @GetMapping("/delete/{login}")
    public String deleteLogin(@PathVariable("login") String login) {
        if (!globalContext.containsChatContext(login)) {
            return "redirect:/";
        }

        seleniumService.stopReplyingTo(login);
        return "redirect:/status?login=" + login;
    }

    @GetMapping("/status")
    public String getStatus(Model model, @RequestParam("login") String login) {
        ChatContext chatCtx = globalContext.getChatContext(login);

        if (chatCtx == null) {
            return "redirect:/";
        }

        List<String> logs = loggerService.getLogs(login);
        if (logs.isEmpty()) {
            logs.add("Whoops... it seems like you didn't authorize in Telegram");
        }

        model.addAttribute("logsList", logs);

        model.addAttribute("isEnabled", chatCtx.isEnabled());
        model.addAttribute("login", chatCtx.getTelegramLogin());
        model.addAttribute("username", chatCtx.getConversatorName());
        model.addAttribute("gender", chatCtx.getConversatorGender());
        model.addAttribute("type", chatCtx.getConversatorType());

        return "success";
    }
}

