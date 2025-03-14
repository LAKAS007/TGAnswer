package org.lakas.personalproject.controllers;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/TGAnswer")
public class MainController {

    @Autowired
    private OpenAiService serviceAI;

    @GetMapping()
    public String index() {
        return "index";
    }


    @GetMapping("/request")
    public String request(@RequestParam("username") String prompt, Model model) {
        System.out.printf(prompt);

        String chatModel = "gpt-4o-mini";

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .model(chatModel)
                .maxTokens(100)
                .temperature(0.7)
                .build();

        String response = serviceAI.createCompletion(request).getChoices().get(0).getText();
        System.out.println(response);

        model.addAttribute("response", response);
        return "answer";
    }
}
