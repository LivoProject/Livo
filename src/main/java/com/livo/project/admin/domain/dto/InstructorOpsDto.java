package com.livo.project.admin.domain.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorOpsDto {
    private String tutorName;
    private long lectures;
    private long total;
    private long confirmed;
    private long pending;
    private long canceled;
    private BigDecimal revenue;
    private double fillRate;
    }
