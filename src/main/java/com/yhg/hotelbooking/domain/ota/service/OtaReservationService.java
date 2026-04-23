package com.yhg.hotelbooking.domain.ota.service;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.allotment.repository.OtaChannelAllotmentRepository;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.inventory.repository.RoomDateInventoryRepository;
import com.yhg.hotelbooking.domain.ota.dto.request.OtaReservationRequest;
import com.yhg.hotelbooking.domain.ota.dto.response.OtaReservationResponse;
import com.yhg.hotelbooking.domain.ota.entity.OtaRequestLog;
import com.yhg.hotelbooking.domain.ota.entity.RequestType;
import com.yhg.hotelbooking.domain.ota.repository.OtaRequestLogRepository;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import com.yhg.hotelbooking.domain.room.repository.RoomTypeRepository;
import com.yhg.hotelbooking.global.config.CustomException;
import com.yhg.hotelbooking.global.config.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OtaReservationService {

    private final OtaRequestLogRepository otaRequestLogRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomDateInventoryRepository roomDateInventoryRepository;
    private final OtaChannelAllotmentRepository otaChannelAllotmentRepository;
    private final ReservationRepository reservationRepository;

    public OtaReservationResponse createReservation(OtaReservationRequest request) {
        // 1. 멱등성 확인

        if (otaRequestLogRepository.existsByOtaChannelAndOtaReservationId(request.getOtaChannel(), request.getOtaReservationId())) {
            throw new CustomException(ErrorCode.HOTEL_ALREADY_EXISTS);
        } else {
            // 2. RoomType 조회

            RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

            for (LocalDate date = request.getCheckInDate(); date.isBefore(request.getCheckOutDate()); date = date.plusDays(1)) {
                /// 3. 날짜별 실제 재고 확인 + 차감
                RoomDateInventory rdi = roomDateInventoryRepository
                        .findByRoomTypeAndDate(roomType, date)  // ← 루프 날짜
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
                // 3-1. 재고 확인 - 0이면 예외
                if (rdi.getAvailableCount() < 1) {
                    throw new CustomException(ErrorCode.INVENTORY_NOT_FOUND);
                }
                rdi.book();
            }


            for (LocalDate date = request.getCheckInDate(); date.isBefore(request.getCheckOutDate()); date = date.plusDays(1)) {
                /// 4. 날짜별 OTA 할당 재고 확인 + 차감
                OtaChannelAllotment rdi = otaChannelAllotmentRepository
                        .findByOtaChannelAndRoomTypeAndDate(request.getOtaChannel(), roomType, date)  // ← 루프 날짜
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
                // 4-1. remaining_count < 1 이면 → 할당소진 예외
                if (rdi.getRemainingCount() < 1) {
                    throw new CustomException(ErrorCode.INVENTORY_NOT_FOUND);
                }
                rdi.book();
            }

            Reservation rv = Reservation.builder()
                    .roomType(roomType)
                    .otaChannel(request.getOtaChannel())
                    .checkOutDate(request.getCheckOutDate())
                    .checkInDate(request.getCheckInDate())
                    .totalPrice(request.getTotalPrice())
                    .guestName(request.getGuestName())
                    .guestPhone(request.getGuestPhone())
                    .build();
            reservationRepository.save(rv);

            OtaRequestLog otaRequestLog = OtaRequestLog.builder()
                    .otaChannel(request.getOtaChannel())
                    .otaReservationId(request.getOtaReservationId())
                    .reservation(rv)
                    .requestType(RequestType.CREATE)
                    .build();
            otaRequestLogRepository.save(otaRequestLog);

            return OtaReservationResponse.from(rv, request.getOtaReservationId());
        }
    }
}
