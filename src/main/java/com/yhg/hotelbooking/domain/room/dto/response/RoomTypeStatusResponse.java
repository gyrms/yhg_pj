package com.yhg.hotelbooking.domain.room.dto.response;

import com.yhg.hotelbooking.domain.room.entity.RoomGrade;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomTypeStatusResponse {

    private final long roomTypeId;
    private final String name;
    private final RoomGrade grade;
    private final Integer totalCount;
    private final Integer bookedCount;
    private final Integer availableCount;
    private final List<OtaAllotmentInfo> otaAllotment;
}
