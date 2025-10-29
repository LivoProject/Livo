package com.livo.project.admin.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class LectureSearch {
    private Integer categoryId;
    private String keyword;
    private String priceType;
    private String status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationStartDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationEndDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lectureStartDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lectureEndDate;
}
