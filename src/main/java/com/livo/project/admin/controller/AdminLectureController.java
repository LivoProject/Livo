package com.livo.project.admin.controller;

import com.livo.project.admin.domain.dto.LectureSearch;
import com.livo.project.admin.service.FileService;
import com.livo.project.admin.service.LectureAdminService;
import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/lecture")
public class AdminLectureController {
    private final LectureAdminService lectureAdminService;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

//    @GetMapping("")
//    public String showLecturePage(){
//        return "admin/lecturePage";
//    }
    @GetMapping("/insert")
    public String showLectureForm(Model model){
        List<Category> parents = categoryRepository.findByParentIsNull();
        model.addAttribute("parents",parents);
        return "admin/lectureForm";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveLecture(@RequestParam("categoryId")int categoryId, @ModelAttribute Lecture lecture){
        Lecture saved = lectureAdminService.saveLecture(lecture, categoryId);
        return ResponseEntity.ok(Map.of("lectureId", saved.getLectureId()));
    }

    @PostMapping("/uploadImage")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
        return fileService.saveFile(file);
    }

    @PostMapping("/delete")
    public String deleteLecture(@RequestParam("lectureId") int id){
        lectureAdminService.deleteLecture(id);
        return "redirect:/admin/lecture";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model, @RequestParam("lectureId") int lectureId){
        Lecture lecture = lectureAdminService.editLecture(lectureId);
        List<Category> parents = categoryRepository.findByParentIsNull();
        model.addAttribute("lecture", lecture);
        model.addAttribute("parents", parents);
        return "admin/lectureEdit";
    }

    @PostMapping("/edit")
    public String editLecture(@RequestParam("categoryId") int categoryId, Lecture lecture){
        lectureAdminService.updateLecture(lecture, categoryId);
        return "redirect:/admin/lecture";
    }

    @GetMapping("/search")
    @ResponseBody
    public Page<Lecture> searchLecture(LectureSearch search,
                              @RequestParam(defaultValue = "0")int page,
                              @RequestParam(defaultValue = "10")int pageSize){
        return lectureAdminService.searchLecture(search, page, pageSize);
    }

}
