package com.yhg.hotelbooking.domain.room.dto.request;

import com.yhg.hotelbooking.domain.room.entity.RoomGrade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomTypeRequest {

    @NotNull(message = "호텔ID를 입력해 주세요.")
    private Long hotelId;

    @NotBlank(message = "객실명을 입력해 주세요.")
    private String name;
    @NotNull(message = "등급을 입력해 주세요.")
    private RoomGrade grade;

    private Integer basePrice;
    private Integer capacity;
    private Integer totalCount;
}
