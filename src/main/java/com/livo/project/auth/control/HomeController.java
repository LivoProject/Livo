// src/main/java/soo/auth/control/HomeController.java
package com.livo.project.auth.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "index"; // /WEB-INF/views/index.jsp
    }
}
