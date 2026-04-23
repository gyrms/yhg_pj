package com.yhg.hotelbooking.domain.room.service;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.allotment.repository.OtaChannelAllotmentRepository;
import com.yhg.hotelbooking.domain.hotel.dto.request.HotelRequest;
import com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse;
import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import com.yhg.hotelbooking.domain.hotel.repository.HotelRepository;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.inventory.repository.RoomDateInventoryRepository;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.dto.request.RoomTypeRequest;
import com.yhg.hotelbooking.domain.room.dto.response.RoomTypeResponse;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import com.yhg.hotelbooking.domain.room.repository.RoomTypeRepository;
import com.yhg.hotelbooking.global.config.CustomException;
import com.yhg.hotelbooking.global.config.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;

    private final RoomDateInventoryRepository roomDateInventoryRepository;
    private final OtaChannelAllotmentRepository otaChannelAllotmentRepository;


    public List<RoomTypeResponse> getAllRoomTypes() {

        return roomTypeRepository.findAll().stream()
                .map(RoomTypeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomTypeResponse createRoomType(RoomTypeRequest request) {

        // Service
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));

        // 2. RoomType 생성 및 저장
        RoomType roomType = RoomType.builder()
                .hotel(hotel)
                .name(request.getName())
                .grade(request.getGrade())
                .basePrice(request.getBasePrice())
                .capacity(request.getCapacity())
                .totalCount(request.getTotalCount())
                .build();

        roomTypeRepository.save(roomType);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(90);

        List<RoomDateInventory> inventories = new ArrayList<>();
        List<OtaChannelAllotment> allotments = new ArrayList<>();

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {

            inventories.add(RoomDateInventory.builder()
                    .roomtype(roomType)
                    .date(date)
                    .build());

            for (OtaChannel ota : OtaChannel.values()) {
                allotments.add(OtaChannelAllotment.builder()
                        .otaChannel(ota)
                        .roomtype(roomType)
                        .date(date)
                        .build());
            }
        }

        roomDateInventoryRepository.saveAll(inventories);
        otaChannelAllotmentRepository.saveAll(allotments);


        // 5. 엔티티 → 응답 DTO 변환 (password 필드 제외)
        return RoomTypeResponse.from(roomType);
    }

}

