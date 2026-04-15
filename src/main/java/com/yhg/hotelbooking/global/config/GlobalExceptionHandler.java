package com.yhg.hotelbooking.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        log.warn("[CustomException] {}: {}", e.getErrorCode(), e.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("status", e.getErrorCode().getStatus().value());
        body.put("message", e.getMessage());

        return ResponseEntity.status(e.getErrorCode().getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("[ValidationException] {}", e.getMessage());

        // 각 필드별 에러 메시지 수집
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("message", ErrorCode.INVALID_INPUT_VALUE.getMessage());
        body.put("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        // 스택 트레이스는 서버 로그에만 기록 (클라이언트에 노출 금지)
        log.error("[UnhandledException] {}", e.getMessage(), e);

        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("message", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

        return ResponseEntity.internalServerError().body(body);
    }
}
