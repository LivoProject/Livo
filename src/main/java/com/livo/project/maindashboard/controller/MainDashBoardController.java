package com.livo.project.maindashboard.controller;

import com.livo.project.maindashboard.domain.dto.MainDashBoardDto;
import com.livo.project.maindashboard.service.MainDashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainDashBoardController {

    private final MainDashBoardService mainService;

    @GetMapping({"/", "/dashboard"})
    public String mainPage(Model model) {
        // 서비스에서 카테고리 + 추천강좌 + 인기강좌 한 번에 조회
        MainDashBoardDto mainDashBoardDto = mainService.getMainPageData();

        // 모델에 담아서 JSP로 전달
        model.addAttribute("categories", mainDashBoardDto.getCategories());
        model.addAttribute("recommendedLectures", mainDashBoardDto.getRecommendedLectures());
        //model.addAttribute("popularLectures", mainDashBoardDto.getPopularLectures());
        model.addAttribute("notices", mainDashBoardDto.getNotices());


        return "maindashboard/dashboard";
    }
}