package com.livo.project.admin.controller;

import com.livo.project.lecture.repository.CategoryRepository;
import com.livo.project.lecture.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("category")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping("/parents")
    public List<Category> getParentCategories(){
        return categoryRepository.findByParentIsNull();
    }

    @GetMapping("/children")
    public List<Category> getChildrenCategories(@RequestParam("parentId")int parentId){
        return categoryRepository.findByParent_CategoryId(parentId);
    }
}
