package com.yhg.hotelbooking.domain.stats.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelBreakdown {

/*  channel, count, revenue*/
    private String channel;
    private long count;
    private double revenue;
}
