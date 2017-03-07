package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PlagcheckController {

    // map a get request to the plagtextForm method
    @GetMapping("/submit")
    public String plagtextForm(Model model) {
        model.addAttribute("plagtext", new Plagtext());
        return "plagtext"; 
    }

    //post requests to the "submit" are handled by plagtextSubmit
    @PostMapping("/submit")
    public String plagtextSubmit(@ModelAttribute Plagtext plagtext) {
        return "result";
    }

}