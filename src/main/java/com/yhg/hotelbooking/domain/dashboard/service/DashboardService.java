package com.yhg.hotelbooking.domain.dashboard.service;

import com.yhg.hotelbooking.domain.dashboard.dto.response.DashboardResponse;
import com.yhg.hotelbooking.domain.dashboard.dto.response.ReservationSummary;
import com.yhg.hotelbooking.domain.ota.dto.request.OtaModifyRequest;
import com.yhg.hotelbooking.domain.ota.dto.response.OtaReservationResponse;
import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.domain.room.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {
    private final ReservationRepository reservationRepository;
    private final RoomTypeRepository roomTypeRepository;
/*    public DashboardResponse getDashboard() {
        List<Reservation> all = reservationRepository.findAll();
        DashboardResponse dResponse = DashboardResponse.from(all, all.size());

        return dResponse;
    }*/
    public DashboardResponse getDashboard() {
        List<Reservation> all = reservationRepository.findAll();
        int totalRoomTypes = (int) roomTypeRepository.count();

        ReservationSummary summary = ReservationSummary.builder()
                .total(all.size())
                .pending(all.stream().filter(r -> r.getStatus() == Reservationstatus.PENDING).count())
                // 나머지 직접 채워봐
                .build();

        List<ReservationResponse> reservations = all.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalRoomTypes(totalRoomTypes)
                .reservationSummary(summary)
                .reservations(reservations)
                .build();
    }
}
