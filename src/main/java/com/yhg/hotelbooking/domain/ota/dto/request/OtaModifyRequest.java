package com.yhg.hotelbooking.domain.ota.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
public class OtaModifyRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;

}
