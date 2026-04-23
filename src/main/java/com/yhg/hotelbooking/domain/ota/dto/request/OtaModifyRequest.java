package com.yhg.hotelbooking.domain.ota.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class OtaModifyRequest {
   // private OtaChannel otaChannel;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;

}
