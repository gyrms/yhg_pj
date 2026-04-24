package com.yhg.hotelbooking.domain.dashboard.dto.response;

import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class DashboardResponse {
    private int totalRoomTypes;
    private ReservationSummary reservationSummary;
    private List<ReservationResponse> reservations;
}

