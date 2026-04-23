package com.yhg.hotelbooking.domain.ota.dto.request;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
public class OtaModifyRequest {
    private OtaChannel otaChannel;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;

}
