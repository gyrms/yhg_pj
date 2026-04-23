package com.yhg.hotelbooking.domain.reservation.controller;

import com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse;
import com.yhg.hotelbooking.domain.hotel.service.HotelService;
import com.yhg.hotelbooking.domain.reservation.dto.response.CheckInResponse;
import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    //  GET    /api/reservations
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> response = reservationService.getAllRs();
        return ResponseEntity.ok(response);
    }

    //  GET    /api/reservations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getOrder(@PathVariable("id") Long rsId) {
        ReservationResponse response = reservationService.getRs(rsId);
        return ResponseEntity.ok(response);
    }

    //POST   /api/reservations/{id}/checkin
    @PostMapping("/ck/{id}/checkin")
    public ResponseEntity<CheckInResponse> getCheckInReservation(@PathVariable("id") Long rsId) {
        CheckInResponse response = reservationService.getCheckInRs(rsId);
        return ResponseEntity.ok(response);
    }

    //POST   /api/reservations/{id}/checkin
    @PostMapping("/{id}/checkin")
    public ResponseEntity<ReservationResponse> setCheckInReservation(@PathVariable("id") Long rsId) {
        ReservationResponse response = reservationService.setCheckInRs(rsId);
        return ResponseEntity.ok(response);
    }

    /// api/reservations/{id}/checkout
    @PostMapping("/{id}/checkout")
    public ResponseEntity<ReservationResponse> setCheckOutReservation(@PathVariable("id") Long rsId) {
        ReservationResponse response = reservationService.setCheckOutRs(rsId);
        return ResponseEntity.ok(response);
    }

    //DELETE /api/reservations/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delReservation(@PathVariable("id") Long rsId) {
        reservationService.deleteRs(rsId);
        return ResponseEntity.noContent().build();
    }
}
