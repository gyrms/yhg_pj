package com.yhg.hotelbooking.domain.reservation.controller;

import com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse;
import com.yhg.hotelbooking.domain.hotel.service.HotelService;
import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService hotelService;
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllHotel() {
        List<ReservationResponse> response = hotelService.getAllHotls();
        return ResponseEntity.ok(response);
    }
}
