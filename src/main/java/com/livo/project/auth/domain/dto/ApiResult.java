package com.livo.project.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult {
    private boolean ok;     // 성공 여부
    private String code;    // BLANK | FORMAT | DUPLICATE | VALID | SERVER_ERROR
    private String message; // 사용자 표시용 메시지
}
