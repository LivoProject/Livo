package com.livo.project.admin.controller;

import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.admin.service.ChapterAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/chapter")
public class ChapterController {
    private final ChapterAdminService chapterAdminService;

    /*챕터 목록(강의별)*/

    /*챕터 등록*/
    @GetMapping("/form")
    public String chapterForm(){
        return "admin/chapterForm";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveAllChapter(@RequestBody List<ChapterList> chapters){
        chapterAdminService.save(chapters);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/list/{lectureId}")
    @ResponseBody
    public List<ChapterList> getChapterList(@PathVariable int lectureId){
        return chapterAdminService.getChaptersByLecture(lectureId);
    }
    @GetMapping("/edit")
    public String editChapterForm(@RequestParam int lectureId, Model model){
        model.addAttribute("lectureId", lectureId);
        return "admin/chapterEdit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<?> editChapter(@RequestBody List<ChapterList> chapters){
        try{
            chapterAdminService.save(chapters);
            return ResponseEntity.ok(Map.of("success", true));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
