package org.lakas.personalproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.model.GlobalContext;
import org.lakas.personalproject.neural.service.NeuralModel;
import org.lakas.personalproject.neural.service.NeuralServiceFactory;
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
    private final NeuralServiceFactory neuralServiceFactory;

    @Autowired
    public MainController(FileLoggerService loggerService, GlobalContext globalContext, SeleniumService seleniumService,
                          NeuralServiceFactory neuralServiceFactory) {
        this.loggerService = loggerService;
        this.globalContext = globalContext;
        this.seleniumService = seleniumService;
        this.neuralServiceFactory = neuralServiceFactory;
    }

    @GetMapping
    public String index() {
        return "in dex";
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

        List<NeuralModel> availableNeuralModels = neuralServiceFactory.getAvailableNeuralModels();
        model.addAttribute("neuralModels", availableNeuralModels);

        model.addAttribute("logsList", logs);

        model.addAttribute("isEnabled", chatCtx.isEnabled());
        model.addAttribute("login", chatCtx.getTelegramLogin());
        model.addAttribute("username", chatCtx.getConversatorName());
        model.addAttribute("gender", chatCtx.getConversatorGender());
        model.addAttribute("type", chatCtx.getConversatorType());

        return "status";
    }
}

