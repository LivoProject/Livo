package com.livo.project.auth.service;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class BusinessException extends RuntimeException {
    private final String field;
    private final HttpStatus status;
    private final Map<String,Object> detail;

    public BusinessException(String field, String message) {
        this(field, message, HttpStatus.BAD_REQUEST, null);
    }

    public BusinessException(String field, String message, HttpStatus status, Map<String,Object> detail) {
        super(message);
        this.field = field;
        this.status = status;
        this.detail = detail;
    }

    public String getField() { return field; }
    public HttpStatus getStatus() { return status; }
    public Map<String,Object> getDetail() { return detail; }
}
