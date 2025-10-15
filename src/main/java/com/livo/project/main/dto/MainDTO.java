package com.livo.project.main.dto;

import com.livo.project.main.domain.Category;
import com.livo.project.main.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MainDTO {
    private List<Category> categories;
    private List<Lecture> lectures;
}