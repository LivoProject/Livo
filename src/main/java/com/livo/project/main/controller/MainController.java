package com.livo.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("index.html")
    public String index(){
        return "main/main";
    }
}
