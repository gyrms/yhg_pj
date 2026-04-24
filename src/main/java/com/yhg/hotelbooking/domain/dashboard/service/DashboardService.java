package com.yhg.hotelbooking.domain.dashboard.service;

import com.yhg.hotelbooking.domain.ota.dto.request.OtaModifyRequest;
import com.yhg.hotelbooking.domain.ota.dto.response.OtaReservationResponse;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {
    private final ReservationRepository reservationRepository;

    public OtaReservationResponse getDashboard() {
        List<Reservation> all = reservationRepository.findAll();
        long pending = all.stream()
                .filter(r -> r.getStatus() == Reservationstatus.PENDING)
                .count();
        return all;
    }

}
