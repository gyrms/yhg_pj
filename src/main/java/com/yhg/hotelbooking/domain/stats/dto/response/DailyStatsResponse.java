package com.yhg.hotelbooking.domain.stats.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyStatsResponse {

    private StatusBreakdown statusBreakdown;
    private ChannelBreakdown channelBreakdown;
    private RoomTypeBreakdown roomTypeBreakdown;
}
