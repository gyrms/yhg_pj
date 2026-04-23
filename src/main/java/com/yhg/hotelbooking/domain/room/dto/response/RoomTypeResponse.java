package com.yhg.hotelbooking.domain.room.dto.response;

import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import com.yhg.hotelbooking.domain.room.entity.RoomGrade;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import lombok.Getter;

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
        this.hotelName = roomtype.getName();
        this.name = roomtype.getName();
        this.grade = roomtype.getGrade();
        this.basePrice = roomtype.getBasePrice();
        this.capacity = roomtype.getCapacity();
        this.totalCount = roomtype.getTotalCount();

    }
}


/*@Getter
public class HotelResponse {
    private final long  id;
    private final String hotelName;
    private final String address;
    private final String description;

    public static com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse from(Hotel hotel) {
        return new com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse(hotel);
    }

    private HotelResponse(Hotel hotel) {
        this.id = hotel.getId();
        this.hotelName = hotel.getName();
        this.address = hotel.getAddress();
        this.description = hotel.getDescription();
    }
}*/
