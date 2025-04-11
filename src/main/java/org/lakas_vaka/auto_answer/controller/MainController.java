package org.lakas_vaka.auto_answer.controller;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.lakas_vaka.auto_answer.model.chat.ConversatorType;
import org.lakas_vaka.auto_answer.model.chat.Gender;
import org.lakas_vaka.auto_answer.neural.service.NeuralModel;
import org.lakas_vaka.auto_answer.neural.service.NeuralServiceFactory;
import org.lakas_vaka.auto_answer.service.auto_answer.AutoAnswerService;
import org.lakas_vaka.auto_answer.session.ChatSession;
import org.lakas_vaka.auto_answer.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping
public class MainController {
    private final FileLogger loggerService;
    private final AutoAnswerService autoAnswerService;
    private final NeuralServiceFactory neuralServiceFactory;
    private final SessionManager sessionManager;

    @Autowired
    public MainController(FileLogger loggerService, AutoAnswerService autoAnswerService,
                          NeuralServiceFactory neuralServiceFactory, SessionManager sessionManager) {
        this.loggerService = loggerService;
        this.autoAnswerService = autoAnswerService;
        this.neuralServiceFactory = neuralServiceFactory;
        this.sessionManager = sessionManager;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute UserInformationForm form) {
        ConversatorContext ctx = new ConversatorContext();

        ctx.setConversatorName(form.getName());
        ctx.setAdditionalInformation(new ArrayList<>());

        if (form.getGender() == null) {
            ctx.setConversatorGender(null);
        } else {
            ctx.setConversatorGender(Gender.valueOf(form.getGender()));
        }
        
        if (form.getType() == null) {
            ctx.setConversatorType(null);
        } else {
            ctx.setConversatorType(ConversatorType.valueOf(form.getType()));
        }

        log.info("User supplied context: {}", ctx);
        autoAnswerService.start(form.getLogin(), ctx);
        return "redirect:/status?login=" + form.getLogin();
    }

    @GetMapping("/delete/{login}")
    public String deleteLogin(@PathVariable("login") String login) {
        if (!sessionManager.isActive(login)) {
            return "redirect:/";
        }

        autoAnswerService.stop(login);
        return "redirect:/status?login=" + login;
    }

    @GetMapping("/status")
    public String getStatus(Model model, @RequestParam("login") String login) {
        ChatSession session = sessionManager.getSession(login);

        if (session == null) {
            return "redirect:/";
        }

        List<String> logs = loggerService.getLogs(login);
        if (logs.isEmpty()) {
            logs.add("Whoops... it seems like you didn't authorize in Telegram");
        }

        List<NeuralModel> availableNeuralModels = neuralServiceFactory.getAvailableNeuralModels();
        model.addAttribute("neuralModels", availableNeuralModels);

        model.addAttribute("logsList", logs);

        model.addAttribute("isEnabled", session.isEnabled());
        model.addAttribute("login", session.getLogin());

        var ctx = session.getConversatorContext();

        model.addAttribute("username", ctx.getConversatorName());
        model.addAttribute("gender", ctx.getConversatorGender());
        model.addAttribute("type", ctx.getConversatorType());

        return "status";
    }
}

