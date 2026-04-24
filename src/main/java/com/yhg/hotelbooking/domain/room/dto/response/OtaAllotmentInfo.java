package com.yhg.hotelbooking.domain.room.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtaAllotmentInfo {
    private final String channel;
    private final Integer remaining;
}
