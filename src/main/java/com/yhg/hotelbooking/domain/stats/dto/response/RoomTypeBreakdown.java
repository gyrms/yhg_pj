package com.yhg.hotelbooking.domain.stats.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomTypeBreakdown {
   /* grade, count, revenue*/
    private String grade;
    private long count;
    private long revenue;

}
