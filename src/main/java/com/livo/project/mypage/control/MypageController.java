package com.livo.project.mypage.control;

import com.livo.project.auth.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final UserService userService;

    /** 내부 폼 DTO (필요 시 별도 패키지로 분리 가능) */
    public record ProfileUpdateRequest(
            @NotBlank(message = "닉네임을 입력해주세요.")
            @Size(min = 2, max = 40, message = "닉네임은 2~40자 사이여야 합니다.")
            String nickname
    ) {}

    public record PasswordChangeRequest(
            @NotBlank(message = "현재 비밀번호를 입력해주세요.")
            String currentPassword,
            @NotBlank(message = "새 비밀번호를 입력해주세요.")
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-]).{8,64}$",
                    message = "영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다."
            )
            String newPassword
    ) {}

    /* --------------------------------------- 기본 페이지들 --------------------------------------- */
    @GetMapping
    public String home() { return "mypage/index"; }

    @GetMapping("/lecture")
    public String lecture() { return "mypage/lecture"; }

    @GetMapping("/bookmark")
    public String bookmark() { return "mypage/bookmark"; }

    @GetMapping("/review")
    public String review() { return "mypage/review"; }

    /* ----------------------------- 회원 정보 (닉네임/비밀번호) ----------------------------- */

    /** ✅ 단일 /info (중복 제거됨) */
    @GetMapping("/info")
    public String info(@AuthenticationPrincipal UserDetails me, Model model) {
        model.addAttribute("email", me.getUsername());
        model.addAttribute("profileForm", new ProfileUpdateRequest(""));
        model.addAttribute("passwordForm", new PasswordChangeRequest("", ""));
        return "mypage/info"; // /WEB-INF/views/mypage/info.jsp
    }

    /** 닉네임 변경 */
    @PostMapping("/info/nickname")
    public String updateNickname(@AuthenticationPrincipal UserDetails me,
                                 @Valid @ModelAttribute("profileForm") ProfileUpdateRequest form,
                                 BindingResult binding,
                                 RedirectAttributes ra,
                                 Model model) {

        if (binding.hasErrors()) {
            model.addAttribute("email", me.getUsername());
            model.addAttribute("passwordForm", new PasswordChangeRequest("", ""));
            return "mypage/info";
        }

        Long userId = userService.findByEmail(me.getUsername()).orElseThrow().getId();
        try {
            userService.updateNickname(userId, form.nickname());
            ra.addFlashAttribute("msg", "닉네임이 변경되었습니다.");
            return "redirect:/mypage/info";
        } catch (Exception e) {
            binding.rejectValue("nickname", "Business", e.getMessage());
            model.addAttribute("email", me.getUsername());
            model.addAttribute("passwordForm", new PasswordChangeRequest("", ""));
            return "mypage/info";
        }
    }

    /** 비밀번호 변경 (성공 시 세션 만료 → 재로그인) */
    @PostMapping("/info/password")
    public String changePassword(@AuthenticationPrincipal UserDetails me,
                                 @Valid @ModelAttribute("passwordForm") PasswordChangeRequest form,
                                 BindingResult binding,
                                 RedirectAttributes ra,
                                 HttpSession session,
                                 Model model) {

        if (binding.hasErrors()) {
            model.addAttribute("email", me.getUsername());
            model.addAttribute("profileForm", new ProfileUpdateRequest(""));
            return "mypage/info";
        }

        Long userId = userService.findByEmail(me.getUsername()).orElseThrow().getId();
        try {
            userService.changePassword(userId, form.currentPassword(), form.newPassword());
            session.invalidate(); // 보안상 재로그인
            ra.addFlashAttribute("msg", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            binding.rejectValue("currentPassword", "Business", e.getMessage());
            model.addAttribute("email", me.getUsername());
            model.addAttribute("profileForm", new ProfileUpdateRequest(""));
            return "mypage/info";
        }
    }
}
