package com.yhg.hotelbooking.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationSummary {
    private long total;
    private long pending;
    private long confirmed;
    private long checkedIn;
    private long cancelled;

}
