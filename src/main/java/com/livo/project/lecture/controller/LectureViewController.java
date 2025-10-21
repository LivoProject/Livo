package com.livo.project.lecture.controller;

import com.livo.project.admin.service.LectureAdminService;
import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.service.ChapterListService;
import com.livo.project.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("lecture")
@RequiredArgsConstructor
public class LectureViewController {
    private final LectureAdminService lectureAdminService;
    private final ChapterListService chapterListService;

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
