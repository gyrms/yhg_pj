package com.yhg.hotelbooking.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    /**
     * 잘못된 요청 파라미터
     */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    /**
     * 잘못된 요청 파라미터
     */
    HOTEL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 호텔입니다"),
    HOTEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 호텔입니다."),
    ROOM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 객실 타입입니다"),
    INVENTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 날짜의 재고 정보가 없습니다."),
    ALLOTMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 OTA의 할당 재고 정보가 없습니다."),

    NOT_CHECKOUT_DATE(HttpStatus.BAD_REQUEST, "체크아웃 날짜가 지났습니다."),
    NOT_DELETE_STATUS(HttpStatus.BAD_REQUEST, "취소할 수 없는 상태입니다."),

    NOT_CHECKIN_STATUS(HttpStatus.BAD_REQUEST, "체크인 상태가 아닙니다."),

    NOT_CONFIRMABLE_STATUS(HttpStatus.BAD_REQUEST, "예약을 할 수있는상태가 아닙니다."),
    CAN_NOT_CHANGE_CONFIRMABLE_STATUS(HttpStatus.BAD_REQUEST, "예약변경을 할 수있는상태가 아닙니다."),
    DUPLICATE_RESERVATION(HttpStatus.CONFLICT, "이미 처리된 예약 요청입니다."),
    NOT_CHECKIN_DATE(HttpStatus.BAD_REQUEST, "체크인 날짜가 아닙니다."),

    INVENTORY_SOLD_OUT(HttpStatus.CONFLICT, "실제 객실 재고가 부족합니다."),
    ALLOTMENT_EXHAUSTED(HttpStatus.CONFLICT, "해당 OTA 채널의 할당 재고가 소진됐습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을수 없습니다. 정보가 없습니다.");


    private final HttpStatus status;
    private final String message;
}
