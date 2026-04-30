package com.yhg.hotelbooking.domain.dashboard.dto.response;

import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {
    private long totalRoomTypes;
    private ReservationSummary reservationSummary;
    private List<ReservationResponse> reservations;
/*

    public static DashboardResponse from(List<Reservation> all, long totalRoomTypes ) {
        return new DashboardResponse(all,totalRoomTypes);
    }

    private DashboardResponse(List<Reservation> all, long totalRoomTypes ) {
        this.totalRoomTypes = totalRoomTypes;
        this.reservationSummary = new ReservationSummary(
                all.size(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.PENDING).count(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.CONFIRMED).count(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.CHECKED_IN).count(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.CANCELLED).count()
        );
        this.reservations = all.stream().map(ReservationResponse::from).collect(java.util.stream.Collectors.toList());

    }
*/


}


