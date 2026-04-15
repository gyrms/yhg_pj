package com.yhg.hotelbooking.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    /** 잘못된 요청 파라미터 */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    /** 잘못된 요청 파라미터 */
    HOTEL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 호텔입니다"),
    HOTEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 호텔입니다."),
    ROOM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 객실 타입입니다"),
    INVENTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 날짜의 재고 정보가 없습니다."),
    ALLOTMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 OTA의 할당 재고 정보가 없습니다.");

    private final HttpStatus status;
    private final String message;
}
