package com.yhg.hotelbooking.domain.stats.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyStatsResponse {

    private String date;
    private int totalReservations;
    private int totalRevenue;
    private StatusBreakdown statusBreakdown;
    private List<ChannelBreakdown> channelBreakdown;    // ← List로
    private List<RoomTypeBreakdown> roomTypeBreakdown;  // ← List로
}
