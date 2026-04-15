package com.yhg.hotelbooking.global.config;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        // 부모 클래스(RuntimeException)에 에러 메시지 전달
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
