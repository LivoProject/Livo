package com.livo.project.uiGuide.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiGuideController {

    @GetMapping("common/ui-guide")
    public String uiGuidePage() {
        return "common/ui-guide";
    }
}