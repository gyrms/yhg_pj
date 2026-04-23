package com.yhg.hotelbooking.domain.hotel.dto.response;

import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import lombok.Getter;

@Getter
public class HotelResponse {
    private final long id;
    private final String hotelName;
    private final String address;
    private final String description;

    public static HotelResponse from(Hotel hotel) {
        return new HotelResponse(hotel);
    }

    private HotelResponse(Hotel hotel) {
        this.id = hotel.getId();
        this.hotelName = hotel.getName();
        this.address = hotel.getAddress();
        this.description = hotel.getDescription();
    }
}
