package com.yhg.hotelbooking.domain.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class NightAuditResponse {

    private int processedCount;
    private List<NoShowReservationResponse> noShowReservations;

    @Getter
    @Builder
    public static class NoShowReservationResponse {
        private Long id;
        private String guestName;
        private LocalDate checkInDate;
    }
}
