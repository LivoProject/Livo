// src/main/java/com/livo/project/admin/controller/AdminPageController.java
package com.livo.project.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    /** 공지 목록 화면 열기 (JSP 렌더링) */
    @GetMapping("/admin/notice")
    public String showNoticePage() {
        return "admin/noticePage"; // /WEB-INF/views/admin/noticePage.jsp
    }
}
