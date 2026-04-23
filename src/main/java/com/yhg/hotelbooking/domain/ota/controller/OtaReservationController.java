package com.yhg.hotelbooking.domain.ota.controller;

import com.yhg.hotelbooking.domain.hotel.dto.request.HotelRequest;
import com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse;
import com.yhg.hotelbooking.domain.hotel.service.HotelService;
import com.yhg.hotelbooking.domain.ota.dto.request.OtaReservationRequest;
import com.yhg.hotelbooking.domain.ota.dto.response.OtaReservationResponse;
import com.yhg.hotelbooking.domain.ota.service.OtaReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ota/reservations")
@RequiredArgsConstructor
public class OtaReservationController {

    private final OtaReservationService otareservationservice;


    @PostMapping
    public ResponseEntity<OtaReservationResponse> createReservation(@Valid @RequestBody OtaReservationRequest request) {
        OtaReservationResponse response = otareservationservice.createReservation(request);
        return ResponseEntity.ok(response);
    }
}
