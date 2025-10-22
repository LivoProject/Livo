package com.livo.project.mypage.controller;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;

import com.livo.project.mypage.domain.dto.MypageDto;
import com.livo.project.mypage.domain.dto.ReservationDto;
import com.livo.project.mypage.service.MypageService;
import com.livo.project.review.domain.Review;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 마이페이지 컨트롤러
 * - 회원 정보 조회, 수정, 비밀번호 변경 요청 처리
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;


    // 마이페이지 메인
    @GetMapping
    public String home(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();

        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {

            provider = (String) oAuthUser.getAttribute("provider");
            email = (String) oAuthUser.getAttribute("email");
        }

        if (email == null) {
            return "redirect:/auth/login";
        }

        MypageDto mypageDto = mypageService.getUserData(email, provider);

        model.addAttribute("mypage", mypageDto);
        model.addAttribute("notices", mypageDto.getNotices());
        model.addAttribute("recommendedLectures", mypageDto.getRecommendedLectures());
        model.addAttribute("top2LikedLectures", mypageService.getTop2LikedLectures(email, provider));

        return "mypage/index";
    }

    // 내 정보 수정
    @GetMapping("/info")
    public String info(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }

        if (email == null) {
            return "redirect:/auth/login";
        }

        MypageDto mypageDto = mypageService.getUserData(email, provider);
        model.addAttribute("mypage", mypageDto);

        return "mypage/info";
    }

    @ResponseBody
    @PostMapping("/update")
    public Map<String, Object> updateUser(MypageDto dto) {
        Map<String, Object> response = new HashMap<>();

        try {
            mypageService.updateUserProfile(dto);
            response.put("success", true);
            response.put("message", "회원 정보가 수정되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }


    // 내 강좌 페이지 이동
    @GetMapping("/lecture")
    public String lecture(Authentication authentication,
                          @PageableDefault(size = 6, direction = Sort.Direction.DESC) Pageable pageable,
                          Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }

        if (email == null) {
            return "redirect:/auth/login";
        }

        Page<ReservationDto> reservations = mypageService.getMyReservations(email, provider, pageable);
        model.addAttribute("reservations", reservations.getContent());
        model.addAttribute("page", reservations);

        return "mypage/lecture";
    }

    //내 강좌 예약 취소
    @ResponseBody
    @PostMapping("/lecture/delete")
    public Map<String, Object> deleteLecture(Authentication authentication,
                                             @RequestParam Integer lectureId) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }

        if (email == null) {
            response.put("success", false);
            response.put("message", "이메일 정보를 찾을 수 없습니다.");
            return response;
        }

        try {
            mypageService.removeReservationLecture(lectureId, email, provider);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }


    // 즐겨찾기 페이지 이동
    @GetMapping("/like")
    public String like(Authentication authentication,
                       @PageableDefault(size = 6, direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider"); // 커스텀 속성 필요
        }

        if (email == null) {
            return "redirect:/auth/login";
        }

        Page<Lecture> likedLectures = mypageService.getLikedLectures(email, provider, pageable);
        model.addAttribute("likedLectures", likedLectures.getContent());
        model.addAttribute("page", likedLectures);
        return "mypage/like";
    }

    @PostMapping("/like/delete")
    @ResponseBody
    public Map<String, Object> deleteLike(Authentication authentication,
                                          @RequestParam Integer lectureId) {

        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider"); // OAuth2User에 provider 속성 매핑 필요
        }

        if (email == null) {
            response.put("success", false);
            response.put("message", "이메일 정보를 찾을 수 없습니다.");
            return response;
        }

        try {
            mypageService.removeLikedLecture(lectureId, email, provider);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 리뷰 페이지
    @GetMapping("/review")
    public String review(@RequestParam(required = false) Integer reviewId,
                         Authentication authentication,
                         @PageableDefault(size = 6, direction = Sort.Direction.DESC)
                         Pageable pageable,
                         Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }

        if (email == null) {
            return "redirect:/auth/login";
        }

        Page<Review> reviews = mypageService.getMyReviews(email, provider, pageable);

        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("page", reviews);

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
