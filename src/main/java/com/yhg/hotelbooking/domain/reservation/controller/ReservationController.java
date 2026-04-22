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

    private final ReservationService hotelService;

    //  GET    /api/reservations
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> response = hotelService.getAllRs();
        return ResponseEntity.ok(response);
    }

  //  GET    /api/reservations/{id}
  @GetMapping("/{id}")
  public ResponseEntity<ReservationResponse> getOrder(@PathVariable("id") Long rsId) {
      ReservationResponse response = hotelService.getRs(rsId);
      return ResponseEntity.ok(response);
  }

    //POST   /api/reservations/{id}/checkin
    @PostMapping("/{id}/checkin")
    public ResponseEntity<CheckInResponse> getCheckinReservations(@PathVariable("id") Long rsId) {
        CheckInResponse response = hotelService.getCheckInRs(rsId);
        return ResponseEntity.ok(response);
    }
}
