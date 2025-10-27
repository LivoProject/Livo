package com.livo.project.lecture.controller;

import com.livo.project.admin.service.LectureAdminService;
import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.service.ChapterListService;
import com.livo.project.mypage.domain.dto.MypageDto;
import com.livo.project.mypage.domain.dto.MypageProgressDto;
import com.livo.project.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureViewController {
    private final LectureAdminService lectureAdminService;
    private final ChapterListService chapterListService;
    private final MypageService mypageService;

    @GetMapping("/view/{lectureId}")
    public String viewLecture(@PathVariable("lectureId") int lectureId,
                              Authentication authentication,
                              Model model) {
        Lecture lecture = lectureAdminService.findById(lectureId);
        List<ChapterList> chapters = chapterListService.getChaptersByLecture(lectureId);
        String youtubeUrl = (chapters != null && !chapters.isEmpty())?chapters.get(0).getYoutubeUrl():null;


        String email = null;
        String provider = null;

        // ✅ 소셜/일반 로그인 모두 대응
        Object principal = authentication != null ? authentication.getPrincipal() : null;
        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }


        MypageProgressDto progressDto = mypageService.getUserProgress(email, provider, lectureId);


        model.addAttribute("lecture", lecture);
        model.addAttribute("chapters", chapters);
        model.addAttribute("youtubeUrl", youtubeUrl);

        model.addAttribute("lastWatchedTime",
                (progressDto != null && progressDto.getLastWatchedTime() != null)
                        ? progressDto.getLastWatchedTime()
                        : 0
        );

        return "mypage/lecture-play";
    }
}
