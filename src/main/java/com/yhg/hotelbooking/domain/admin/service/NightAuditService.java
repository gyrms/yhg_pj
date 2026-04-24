package com.yhg.hotelbooking.domain.admin.service;

import com.yhg.hotelbooking.domain.admin.dto.response.NightAuditResponse;
import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.global.config.CustomException;
import com.yhg.hotelbooking.global.config.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NightAuditService {

    private final ReservationRepository reservationRepository;

    public NightAuditResponse findCheckoutDataToday() {
        List<Reservation> rs = reservationRepository.findCheckoutDataToday(LocalDate.now());
        List<NightAuditResponse.NoShowReservationResponse> noShowReservations = rs.stream()
                .map(reservation -> NightAuditResponse.NoShowReservationResponse.builder()
                        .id(reservation.getId())
                        .guestName(reservation.getGuestName())
                        .checkInDate(reservation.getCheckInDate())
                        .build())
                .toList();

        return NightAuditResponse.builder()
                .processedCount(rs.size())
                .noShowReservations(noShowReservations)
                .build();


    }
}
