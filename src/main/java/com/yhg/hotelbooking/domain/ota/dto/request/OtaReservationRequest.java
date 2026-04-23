package com.yhg.hotelbooking.domain.ota.dto.request;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class OtaReservationRequest {
    private OtaChannel otaChannel;
    private String otaReservationId;
    private Long roomTypeId;
    private String guestName;
    private String guestPhone;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;
}

