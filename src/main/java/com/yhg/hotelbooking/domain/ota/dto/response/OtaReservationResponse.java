package com.yhg.hotelbooking.domain.ota.dto.response;

import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import lombok.Getter;

@Getter
public class OtaReservationResponse {
    private final Long reservationId;       // 우리 시스템 예약 ID
    private final String otaReservationId;  // OTA 측 예약 ID
    private final Reservationstatus status; // PENDING

    public static OtaReservationResponse from(Reservation reservation, String otaReservationId) {
        return new OtaReservationResponse(reservation, otaReservationId);
    }

    private OtaReservationResponse(Reservation reservation, String otaReservationId) {
        this.reservationId = reservation.getId();
        this.otaReservationId = otaReservationId;
        this.status = reservation.getStatus();
    }
}