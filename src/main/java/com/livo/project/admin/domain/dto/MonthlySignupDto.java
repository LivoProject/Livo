package com.livo.project.admin.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySignupDto {

    private String ym;       // YYYY-MM
    private long newUsers;   // 신규 가입자 수
}
