package com.yhg.hotelbooking.domain.stats.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChannelStatsResponse {
    private String channel;
    private String date;
    private int totalReservations;
    private int totalRevenue;
    private double avgNights;
    private StatusBreakdown statusBreakdown;
    private List<RoomTypeBreakdown> roomTypeBreakdown;  // ← List로

}
