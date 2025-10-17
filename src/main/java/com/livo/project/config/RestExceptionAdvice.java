// com/livo/project/config/RestExceptionAdvice.java
package com.livo.project.config;

import com.livo.project.auth.service.BusinessException; // ★ 추가
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class RestExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleBind(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDup(DataIntegrityViolationException ex) {
        String root = Optional.ofNullable(ex.getMostSpecificCause()).map(Throwable::getMessage).orElse("");
        Map<String, String> err = new HashMap<>();
        if (root.contains("uk_user_email")) err.put("email", "이미 등록된 이메일입니다.");
        else if (root.contains("uq_user_nickname")) err.put("nickname", "이미 사용 중인 닉네임입니다.");
        else if (root.contains("uq_user_phone")) err.put("phone", "이미 등록된 전화번호입니다.");
        else err.put("error", "이미 사용 중인 정보가 있습니다.");
        return ResponseEntity.badRequest().body(err);
    }

    // ★ BusinessException 전용
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBiz(BusinessException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (ex.getField() != null) {
            body.put(ex.getField(), ex.getMessage()); // { "email": "이미 사용 중..." } 같은 형태
        } else {
            body.put("error", ex.getMessage());
        }
        if (ex.getDetail() != null) body.putAll(ex.getDetail());
        HttpStatus st = (ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(st).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleEtc(Exception ex) {
        log.error("[REST-ERROR] {}", ex.toString(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", Optional.ofNullable(ex.getMessage()).orElse("서버 오류가 발생했습니다."));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
