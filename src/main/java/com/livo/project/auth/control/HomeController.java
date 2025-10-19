// src/main/java/soo/auth/control/HomeController.java

package com.livo.project.auth.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 루트로 오면 dashboard.jsp 렌더
    @GetMapping({"/home"})
    public String main() {
        return "main/main"; // /WEB-INF/views/main/dashboard.jsp
    }
}
