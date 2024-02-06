package org.project.simproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestViewController {
    @GetMapping("/oauthLogin")
    public String oauthLogin(){
        return "oauthLogin";
    }
}
