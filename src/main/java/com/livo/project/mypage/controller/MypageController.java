package com.livo.project.mypage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.lecture.domain.Lecture;

import com.livo.project.mypage.domain.dto.MypageDto;
import com.livo.project.mypage.domain.dto.MypageLikedLectureDto;
import com.livo.project.mypage.domain.dto.MypageProgressDto;
import com.livo.project.mypage.domain.dto.MypageReservationDto;
import com.livo.project.mypage.domain.entity.LectureProgress;
import com.livo.project.mypage.repository.MypageReservationRepository;
import com.livo.project.mypage.service.MypageService;
import com.livo.project.payment.domain.Payment;
import com.livo.project.review.domain.Review;
import com.livo.project.utils.AuthUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
/**
 * 마이페이지 컨트롤러
 * - 회원 정보 조회, 수정, 비밀번호 변경 요청 처리
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;
    private final MypageReservationRepository mypageReservationRepository;

    // 마이페이지 메인
    @GetMapping
    public String home(Model model) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) return "redirect:/auth/login";

        MypageDto mypageDto = mypageService.getUserData(email, provider);
        LectureProgress recentProgress = mypageService.getRecentLecture(email);
        long inProgressLectureCount = mypageService.getInProgressLectureCount(email);
        double weeklyStudyHours = mypageService.getWeeklyStudyHours(email);
        List<Payment> payments = mypageService.getRecentPayments(email, 3);
        List<MypageReservationDto> recentConfirmedLectures =
                mypageService.getRecentConfirmedLectures(email, provider);

        model.addAttribute("recentConfirmedLectures", recentConfirmedLectures);

        model.addAttribute("mypage", mypageDto);
        model.addAttribute("notices", mypageDto.getNotices());
        model.addAttribute("recommendedLectures", mypageDto.getRecommendedLectures());
        model.addAttribute("top2LikedLectures", mypageService.getTop2LikedLectures(email, provider));
        model.addAttribute("recentLecture", recentProgress);
        model.addAttribute("totalStudyHours", mypageService.getTotalStudyHours(email));
        model.addAttribute("completedLectures", mypageService.getCompletedLectureCount(email));
        model.addAttribute("studyDays", mypageService.getStudyDaysThisMonth(email));
        model.addAttribute("weeklyStudyHours", String.format("%.0f", weeklyStudyHours));
        model.addAttribute("inProgressLectureCount", inProgressLectureCount);
        model.addAttribute("payments", payments);
        model.addAttribute("menu", "home");

        return "mypage/index";
    }

    // 내 정보 수정
    @GetMapping("/info")
    public String info(Model model) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) return "redirect:/auth/login";

        MypageDto mypageDto = mypageService.getUserData(email, provider);
        model.addAttribute("mypage", mypageDto);

        model.addAttribute("menu", "info");

        return "mypage/info";
    }

    @ResponseBody
    @PostMapping("/update")
    public Map<String, Object> updateUser(MypageDto dto) {
        Map<String, Object> result = new HashMap<>();
        try {
            mypageService.updateUserProfile(dto);
            result.put("success", true);
            result.put("message", "회원 정보가 수정되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }


    // 내 강좌 페이지 이동
    @GetMapping("/lecture")
    public String lecture(@PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                          Model model) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) return "redirect:/auth/login";


        MypageDto mypageDto = mypageService.getUserData(email, provider);
        Page<MypageReservationDto> reservations = mypageService.getMyReservations(email, provider, pageable);

        model.addAttribute("mypage", mypageDto);
        model.addAttribute("reservations", reservations.getContent());
        model.addAttribute("page", reservations);
        model.addAttribute("menu", "lecture");
        model.addAttribute("today", java.time.LocalDate.now());
        return "mypage/lecture";
    }

    //내 강좌 예약 취소
    @ResponseBody
    @PostMapping("/lecture/delete")
    public Map<String, Object> deleteLecture(@RequestParam Integer lectureId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");

        if (email == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            mypageService.removeReservationLecture(lectureId, email);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }


    // 즐겨찾기 페이지 이동
    @GetMapping("/like")
    public String like(@PageableDefault(size = 6) Pageable pageable, Model model) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) return "redirect:/auth/login";

        Page<MypageLikedLectureDto> likedLectures = mypageService.getLikedLecturesWithProgress(email, provider, pageable);
        MypageDto mypageDto = mypageService.getUserData(email, provider);

        model.addAttribute("mypage", mypageDto);
        model.addAttribute("likedLectures", likedLectures.getContent());
        model.addAttribute("page", likedLectures);
        model.addAttribute("menu", "like");

        return "mypage/like";
    }

    @PostMapping("/like/delete")
    @ResponseBody
    public Map<String, Object> deleteLike(@RequestParam Integer lectureId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
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
    public String review(@PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                         Model model) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) return "redirect:/auth/login";

        Page<Review> reviews = mypageService.getMyReviews(email, pageable);
        MypageDto mypageDto = mypageService.getUserData(email, provider);

        model.addAttribute("mypage", mypageDto);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("page", reviews);
        model.addAttribute("menu", "review");

        return "mypage/review";
    }

    // 비밀번호 변경 페이지 이동
    @GetMapping("/password")
    public String showPasswordChangePage() {
        return "mypage/password";
    }

    // 비밀번호 변경 처리
    @PostMapping("/password")
    public String updatePassword(@RequestParam String currentPassword,
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
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "mypage/password";
    }


    // 현재 진행률 저장
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> saveProgress(@RequestBody MypageProgressDto dto) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        mypageService.saveProgress(email, provider, dto);
        return ResponseEntity.ok("진행률 저장 완료");
    }


    // 결제 내역
    @GetMapping("/payment")
    public String payment(Model model, Pageable pageable) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) return "redirect:/auth/login";

        MypageDto mypageDto = mypageService.getUserData(email, provider);
        Page<Payment> payments = mypageService.getMyPayments(email, pageable);

        model.addAttribute("mypage", mypageDto);
        model.addAttribute("payments", payments.getContent());
        model.addAttribute("page", payments);
        model.addAttribute("menu", "payment");

        return "mypage/payment";
    }

    // 검색
    @PostMapping("/lecture/search")
    @ResponseBody
    public Map<String, Object> searchLecturesAjax(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
            Pageable pageable
    ) {
        Map<String, String> user = AuthUtil.getLoginUserInfo();
        String email = user.get("email");
        String provider = user.get("provider");

        if (email == null) {
            return Map.of("success", false, "error", "UNAUTHORIZED");
        }

        Page<MypageReservationDto> reservations =
                mypageService.searchMyReservations(email, provider, keyword, sort, pageable);

        return Map.of("success", true, "data", reservations.getContent());

    }

}
