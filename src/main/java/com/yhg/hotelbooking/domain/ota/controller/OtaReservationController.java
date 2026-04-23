package com.yhg.hotelbooking.domain.ota.controller;

import com.yhg.hotelbooking.domain.ota.dto.request.OtaModifyRequest;
import com.yhg.hotelbooking.domain.ota.dto.request.OtaReservationRequest;
import com.yhg.hotelbooking.domain.ota.dto.response.OtaReservationResponse;
import com.yhg.hotelbooking.domain.ota.service.OtaReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ota/reservations")
@RequiredArgsConstructor
public class OtaReservationController {

    private final OtaReservationService otaReservationService;

    @PostMapping
    public ResponseEntity<OtaReservationResponse> createReservation(@Valid @RequestBody OtaReservationRequest request) {
        OtaReservationResponse response = otaReservationService.createReservation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{otaResId}/confirm")
    public ResponseEntity<OtaReservationResponse> confirm(@PathVariable("otaResId") String otaResId) {
        OtaReservationResponse response = otaReservationService.confirm(otaResId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{otaResId}")
    public ResponseEntity<Void> Delete(@PathVariable("otaResId") String otaResId) {
        otaReservationService.delete(otaResId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{otaResId}")
    public ResponseEntity<OtaReservationResponse> update(@PathVariable("otaResId") String otaResId,@Valid @RequestBody OtaModifyRequest request) {
        OtaReservationResponse response = otaReservationService.update(otaResId,request);
        return ResponseEntity.ok(response);
    }
}
