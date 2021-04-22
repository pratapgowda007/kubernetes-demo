package com.nsc.kubernetes.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping("/login")
    @CrossOrigin
    public String login() {
        return "login";
    }
}

