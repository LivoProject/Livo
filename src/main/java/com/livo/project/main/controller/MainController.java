package com.livo.project.main.controller;

import com.livo.project.main.dto.MainDTO;
import com.livo.project.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor // ✅ 자동으로 생성자 주입 (final 필드 자동 주입)
public class MainController {

    private final MainService mainService;

    @GetMapping({"/", "/main"})
    public String mainPage(Model model) {
        // ✅ 서비스에서 카테고리 + 추천강좌 + 인기강좌 한 번에 조회
        MainDTO mainData = mainService.getMainPageData();

        // ✅ 모델에 담아서 JSP or Thymeleaf로 전달
        model.addAttribute("categories", mainData.getCategories());
        model.addAttribute("recommendedLectures", mainData.getRecommendedLectures());
        //model.addAttribute("popularLectures", mainData.getPopularLectures());

        // ✅ main.jsp or main.html 로 이동
        return "main/main";
    }
}