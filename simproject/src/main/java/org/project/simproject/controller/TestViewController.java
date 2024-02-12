package org.project.simproject.controller;

import lombok.RequiredArgsConstructor;
import org.project.simproject.dto.AddUserRequest;
import org.project.simproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TestViewController {
    private final UserService userService;

    @PostMapping("/signup")
    public String save(AddUserRequest addUserRequest){
        userService.createUser(addUserRequest);
        return "redirect:/login";
    }

    @GetMapping("/oauthLogin")
    public String oauthLogin(){
        return "oauthLogin";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/signup")
    public String signUp(){
        return "signup";
    }
}
