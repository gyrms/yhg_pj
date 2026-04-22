package com.yhg.hotelbooking.domain.hotel.service;

import com.yhg.hotelbooking.domain.hotel.dto.request.HotelRequest;
import com.yhg.hotelbooking.domain.hotel.dto.response.HotelResponse;
import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import com.yhg.hotelbooking.domain.hotel.repository.HotelRepository;
import com.yhg.hotelbooking.global.config.CustomException;
import com.yhg.hotelbooking.global.config.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelService {
    private final HotelRepository hotelRepository;


    @Transactional
    public HotelResponse enroll(HotelRequest request) {

        // Service
        if (hotelRepository.existsByAddress(request.getHotelAddress())) {
            throw new CustomException(ErrorCode.HOTEL_ALREADY_EXISTS);
        }
        // 3. Member 엔티티 생성 (Builder 패턴)
        Hotel hotel = Hotel.builder()
                .name(request.getHotelName())
                .address(request.getHotelAddress())
                .description(request.getHotelDescription())
                .build();


        // 4. DB 저장 (save 호출 시 INSERT 쿼리 실행)
        Hotel savedHotel = hotelRepository.save(hotel);

        // 5. 엔티티 → 응답 DTO 변환 (password 필드 제외)
        return HotelResponse.from(savedHotel);
    }


    public List<HotelResponse> getAllHotls() {

        return hotelRepository.findAll().stream()
                .map(HotelResponse::from)
                .collect(Collectors.toList());
    }
}
