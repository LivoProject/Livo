package com.livo.project.auth.service;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

public class BusinessException extends RuntimeException {
    private final String field;                 // null이면 글로벌 에러로 처리
    private final HttpStatus status;            // 기본 400
    private final Map<String, Object> detail;   // 부가정보(선택)

    public BusinessException(String field, String message) {
        this(field, message, HttpStatus.BAD_REQUEST, null);
    }

    public BusinessException(String field, String message, HttpStatus status) {
        this(field, message, status, null);
    }

    /** 글로벌 에러(필드 미지정) 용도 */
    public BusinessException(String message) {
        this(null, message, HttpStatus.BAD_REQUEST, null);
    }

    public BusinessException(String field, String message, HttpStatus status, Map<String, Object> detail) {
        super(message);
        this.field = field;
        this.status = (status != null ? status : HttpStatus.BAD_REQUEST);
        this.detail = (detail != null ? detail : Collections.emptyMap());
    }

    public String getField() { return field; }
    public HttpStatus getStatus() { return status; }
    public Map<String,Object> getDetail() { return detail; }

    @Override public String toString() {
        return "BusinessException{field=" + field + ", status=" + status + ", message=" + getMessage() + "}";
    }

    // 선택) 자주 쓰는 정적 팩토리
    public static BusinessException badRequest(String field, String msg){ return new BusinessException(field, msg, HttpStatus.BAD_REQUEST); }
    public static BusinessException conflict(String field, String msg){ return new BusinessException(field, msg, HttpStatus.CONFLICT); }
}
