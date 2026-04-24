package com.yhg.hotelbooking.domain.reservation.dto.response;

import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ReservationResponse {
    private final Long id;
    private final String otaChannel;
    private final String guestName;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final Integer nights;
    private final String status;
    private final LocalDateTime createdAt;

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation);
    }

    private ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.otaChannel = reservation.getOtaChannel().name();
        this.guestName = reservation.getGuestName();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();
        this.nights = reservation.getNights();
        this.status = reservation.getStatus().name();
        this.createdAt = reservation.getCreatedAt();

    }
}
