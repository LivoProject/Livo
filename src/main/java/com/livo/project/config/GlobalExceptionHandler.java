package com.livo.project.config;

import com.livo.project.auth.service.BusinessException;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource ms;
    public GlobalExceptionHandler(MessageSource ms){ this.ms = ms; }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> body(MethodArgumentNotValidException ex, Locale locale) {
        return ResponseEntity.badRequest().body(toMap(ex.getBindingResult(), locale));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String,String>> bind(BindException ex, Locale locale) {
        return ResponseEntity.badRequest().body(toMap(ex.getBindingResult(), locale));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String,String>> business(BusinessException ex) {
        String field = ex.getField();
        String msg = ex.getMessage();
        if (field != null)
            return ResponseEntity.status(ex.getStatus()).body(Map.of(field, msg));
        return ResponseEntity.status(ex.getStatus()).body(Map.of("error", msg));
    }

    private Map<String,String> toMap(BindingResult br, Locale locale) {
        Map<String,String> out = new LinkedHashMap<>();
        br.getFieldErrors().forEach(e -> out.put(e.getField(), ms.getMessage(e, locale)));
        return out;
    }
}
