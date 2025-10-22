package com.livo.project.mypage.controller;

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
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";

        String email = userDetails.getUsername();
        MypageDto mypateDto = mypageService.getUserData(email);

        model.addAttribute("mypage", mypateDto);
        model.addAttribute("notices", mypateDto.getNotices());
        model.addAttribute("recommendedLectures", mypateDto.getRecommendedLectures());
        List<Lecture> top2LikedLectures = mypageService.getTop2LikedLectures(email);
        model.addAttribute("top2LikedLectures", top2LikedLectures);
        model.addAttribute("fr", top2LikedLectures);
        return "mypage/index";
    }

    // 내 정보 조회
    @GetMapping("/info")
    public String info(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        MypageDto mypage = mypageService.getUserData(email);
        model.addAttribute("mypage", mypage);
        return "mypage/info";
    }

    // 내 정보 수정
    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute MypageDto mypageDto) {
        String email = userDetails.getUsername();
        mypageService.updateUserProfile(email, mypageDto);
        return "redirect:/mypage/info";
    }

    // 내 강좌 페이지 이동
    @GetMapping("/lecture")
    public String lecture(@AuthenticationPrincipal UserDetails userDetails,
                          @PageableDefault(size = 6, direction = Sort.Direction.DESC)
                          Pageable pageable,
                          Model model) {

        String email = userDetails.getUsername();
        Page<ReservationDto> reservations = mypageService.getMyReservations(email, pageable);
        model.addAttribute("reservations", reservations.getContent());
        model.addAttribute("page", reservations);

        return "mypage/lecture";
    }

    //내 강좌 예약 취소
    @ResponseBody
    @PostMapping("/lecture/delete")
    public Map<String, Object> deleteLecture(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam Integer lectureId) {
        Map<String, Object> response = new HashMap<>();

        if (userDetails == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            String email = userDetails.getUsername();
            mypageService.removeReservationLecture(email, lectureId);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 즐겨찾기 페이지 이동
    @GetMapping("/like")
    public String like(@AuthenticationPrincipal UserDetails userDetails,
                       @PageableDefault(size = 6, direction = Sort.Direction.DESC)
                       Pageable pageable,
                       Model model) {
        String email = userDetails.getUsername();
        Page<Lecture> likedLectures = mypageService.getLikedLectures(email, pageable);
        model.addAttribute("likedLectures", likedLectures.getContent());
        model.addAttribute("page", likedLectures);
        return "mypage/like";
    }

    // 즐겨찾기 해제
    @PostMapping("/like/delete")
    @ResponseBody
    public Map<String, Object> deleteLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer lectureId) {

        Map<String, Object> response = new HashMap<>();

        if (userDetails == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            String email = userDetails.getUsername();
            mypageService.removeLikedLecture(lectureId, email);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 리뷰 페이지 이동
    @GetMapping("/review")
    public String review(@RequestParam(required = false) Integer reviewId,
                         @AuthenticationPrincipal UserDetails userDetails,
                         @PageableDefault(size = 6, direction = Sort.Direction.DESC)
                         Pageable pageable,
                         Model model
    ) {
        String email = userDetails.getUsername();

        Page<Review> reviews = mypageService.getMyReviews(email, pageable);
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
