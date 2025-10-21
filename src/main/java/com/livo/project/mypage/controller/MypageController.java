package com.livo.project.mypage.controller;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.mypage.domain.dto.MypageDto;
import com.livo.project.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 마이페이지 컨트롤러
 * - 회원 정보 조회, 수정, 비밀번호 변경 요청 처리
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;


    /** 마이페이지 메인 */
    @GetMapping
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 로그인 체크 (앱 로그인 페이지 경로에 맞춰서)
        if (userDetails == null) return "redirect:/auth/login";

        MypageDto mypageDto = mypageService.getUserData();   // ✅ email 안 넘김
        model.addAttribute("mypage", mypageDto);
        model.addAttribute("notices", mypageDto.getNotices());
        model.addAttribute("recommendedLectures", mypageDto.getRecommendedLectures());
        return "mypage/index";
    }

    /** 내 정보 화면 */
    @GetMapping("/info")
    public String info(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";

        MypageDto mypage = mypageService.getUserData();      // ✅ email 안 넘김
        model.addAttribute("mypage", mypage);
        return "mypage/info";
    }

    /** 내 정보 수정 */
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute MypageDto mypageDto) {
        mypageService.updateUserProfile(mypageDto);          // ✅ email 안 넘김
        return "redirect:/mypage/info";
    }

    // 내 강좌 페이지 이동
    @GetMapping("/lecture")
    public String lecture() {
        return "mypage/lecture";
    }

    // 즐겨찾기 페이지 이동
    @GetMapping("/bookmark")
    public String bookmark() {
        return "mypage/bookmark";
    }

    // 리뷰 페이지 이동
    @GetMapping("/review")
    public String review() {
        return "mypage/review";
    }

    // 비밀번호 변경 페이지 이동
    @GetMapping("/password")
    public String showPasswordChangePage() {
        return "mypage/password";
    }

    // 비밀번호 변경 처리
    @PostMapping("/password")
    public String updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "mypage/password";
        }

        try {
            mypageService.updateUserPassword(currentPassword, newPassword);
            model.addAttribute("success", "비밀번호가 성공적으로 변경되었습니다.");
            return "mypage/password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "mypage/password";
        }
    }

}
