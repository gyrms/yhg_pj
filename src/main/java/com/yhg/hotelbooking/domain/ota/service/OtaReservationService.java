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
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import com.yhg.hotelbooking.domain.room.repository.RoomTypeRepository;
import com.yhg.hotelbooking.global.config.CustomException;
import com.yhg.hotelbooking.global.config.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class OtaReservationService {

    private final OtaRequestLogRepository otaRequestLogRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomDateInventoryRepository roomDateInventoryRepository;
    private final OtaChannelAllotmentRepository otaChannelAllotmentRepository;
    private final ReservationRepository reservationRepository;

    public OtaReservationResponse confirm(Long otaResId) {

        OtaRequestLog otaRequestLog = otaRequestLogRepository.findById(otaResId).orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        Reservation rv = otaRequestLog.getReservation();
        if(rv.getStatus()== Reservationstatus.PENDING){
            rv.confirm();
        }
        return OtaReservationResponse.from(rv, otaRequestLog.getOtaReservationId());

     /*   1. otaReservationId 로 예약 조회
          ↓
        2. 상태가 PENDING 인지 확인
          ↓
        3. CONFIRMED 로 변경
          ↓
        4. OtaRequestLog 기록 (CONFIRM)*/

    }
    public OtaReservationResponse createReservation(OtaReservationRequest request) {
        // 1. 멱등성 확인
        if (otaRequestLogRepository.existsByOtaChannelAndOtaReservationId(request.getOtaChannel(), request.getOtaReservationId())) {
            throw new CustomException(ErrorCode.HOTEL_ALREADY_EXISTS);
        }

        // 2. RoomType 조회
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        checkRoomInventory(request, roomType, false);
        checkOtaAllotment(request, roomType, false);

        checkRoomInventory(request, roomType, true);
        checkOtaAllotment(request, roomType, true);


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
                .build();
        otaRequestLogRepository.save(otaRequestLog);

        return OtaReservationResponse.from(rv, request.getOtaReservationId());

    }

    public void setDeleteReservation(Long otaResId) {

        OtaRequestLog otaRequestLog = otaRequestLogRepository.findById(otaResId).orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));

        Reservation rv = otaRequestLog.getReservation();
        rv.cancel();

        RoomType roomType = roomTypeRepository.findById(rv.getRoomType().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        for (LocalDate date = rv.getCheckInDate(); date.isBefore(rv.getCheckOutDate()); date = date.plusDays(1)) {
            RoomDateInventory rdi = roomDateInventoryRepository
                    .findByRoomTypeAndDate(roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

            rdi.restore();

        }

        for (LocalDate date = rv.getCheckInDate(); date.isBefore(rv.getCheckOutDate()); date = date.plusDays(1)) {
            OtaChannelAllotment ota = otaChannelAllotmentRepository
                    .findByOtaChannelAndRoomTypeAndDate(rv.getOtaChannel(), roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.ALLOTMENT_NOT_FOUND));

            ota.cancel();

        }
        otaRequestLog.cancel();
    }


    private void checkRoomInventory(OtaReservationRequest request, RoomType roomType, boolean isplay) {

        // 5. 실제 재고 차감
        for (LocalDate date = request.getCheckInDate(); date.isBefore(request.getCheckOutDate()); date = date.plusDays(1)) {
            RoomDateInventory rdi = roomDateInventoryRepository
                    .findByRoomTypeAndDate(roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            if (isplay) {
                rdi.book();
            } else {
                if (rdi.getAvailableCount() < 1) {
                    throw new CustomException(ErrorCode.INVENTORY_NOT_FOUND);
                }
            }
        }
    }

    private void checkOtaAllotment(OtaReservationRequest request, RoomType roomType, boolean isplay) {

        for (LocalDate date = request.getCheckInDate(); date.isBefore(request.getCheckOutDate()); date = date.plusDays(1)) {
            OtaChannelAllotment ota = otaChannelAllotmentRepository
                    .findByOtaChannelAndRoomTypeAndDate(request.getOtaChannel(), roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.ALLOTMENT_NOT_FOUND));
            if (isplay) {
                ota.book();
            } else {
                if (ota.getRemainingCount() < 1) {
                    throw new CustomException(ErrorCode.ALLOTMENT_NOT_FOUND);
                }
            }
        }

    }
}
