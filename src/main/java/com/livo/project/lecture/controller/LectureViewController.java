package com.livo.project.lecture.controller;

import com.livo.project.admin.service.LectureAdminService;
import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.service.ChapterListService;
import com.livo.project.lecture.service.LectureService;
import com.livo.project.mypage.domain.dto.ProgressDto;
import com.livo.project.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("lecture")
@RequiredArgsConstructor
public class LectureViewController {
    private final LectureAdminService lectureAdminService;
    private final ChapterListService chapterListService;
    private final MypageService mypageService;

    @GetMapping("/view/{lectureId}")
    public String viewLecture(@PathVariable("lectureId") int lectureId, Model model) {
        Lecture lecture = lectureAdminService.findById(lectureId);
        List<ChapterList> chapters = chapterListService.getChaptersByLecture(lectureId);
        String youtubeUrl = (chapters != null && !chapters.isEmpty())?chapters.get(0).getYoutubeUrl():null;

        model.addAttribute("lecture", lecture);
        model.addAttribute("chapters", chapters);
        model.addAttribute("youtubeUrl", youtubeUrl);
        return "mypage/lecture-play";
    }
}
