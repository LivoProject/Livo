package com.livo.project.search.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
    private int lectureId;
    private String title;
    private String tutorName;
    private String content;
    private String thumbnail;
    private int price;
}
