package com.yhg.hotelbooking.domain.room.dto.response;

import com.yhg.hotelbooking.domain.room.entity.RoomGrade;
import com.yhg.hotelbooking.domain.room.entity.RoomType;

public class RoomTypeResponse {

    private final long id;
    private final String hotelName;
    private final String name;
    private final RoomGrade grade;
    private final Integer basePrice;
    private final Integer capacity;
    private final Integer totalCount;

    public static RoomTypeResponse from(RoomType roomtype) {
        return new RoomTypeResponse(roomtype);
    }

    private RoomTypeResponse(RoomType roomtype) {
        this.id = roomtype.getId();
        this.hotelName = roomtype.getHotel().getName();
        this.name = roomtype.getName();
        this.grade = roomtype.getGrade();
        this.basePrice = roomtype.getBasePrice();
        this.capacity = roomtype.getCapacity();
        this.totalCount = roomtype.getTotalCount();
    }
}
