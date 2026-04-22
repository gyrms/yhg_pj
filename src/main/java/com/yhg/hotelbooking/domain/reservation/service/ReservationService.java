package com.yhg.hotelbooking.domain.reservation.service;

import com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse;
import com.yhg.hotelbooking.domain.hotel.repository.HotelRepository;
import com.yhg.hotelbooking.domain.reservation.dto.response.CheckInResponse;
import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.global.config.CustomException;
import com.yhg.hotelbooking.global.config.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationResponse> getAllRs() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    public ReservationResponse getRs(Long rsId) {
        Reservation rs = reservationRepository.findById(rsId).orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        return ReservationResponse.from(rs);
    }

    public CheckInResponse getCheckInRs(Long rsId) {
        return reservationRepository.findConfirmedById(rsId)
                .map(CheckInResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

    }


}
