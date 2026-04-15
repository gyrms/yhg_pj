package com.yhg.hotelbooking.domain.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HotelRequest {

    @NotBlank(message = "호텔명을 입력해 주세요.")
    private String hotelName;
    @NotBlank(message = "호텔주소를 입력해 주세요.")
    private String hotelAddress;
    private String hotelDescription;

}
