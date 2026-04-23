package com.yhg.hotelbooking.domain.ota.service;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.allotment.repository.OtaChannelAllotmentRepository;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.inventory.repository.RoomDateInventoryRepository;
import com.yhg.hotelbooking.domain.ota.dto.request.OtaModifyRequest;
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


    public OtaReservationResponse update(String otaResId, OtaModifyRequest request) {
   /*     1. otaReservationId 로 OtaRequestLog 조회
        2. 연결된 Reservation 꺼내기
        3. PENDING or CONFIRMED 상태인지 확인 (취소된 건 수정 불가)
        4. 기존 날짜별 재고 원복 (기존 체크인~아웃)
        5. 새 날짜 재고 확인 (새 체크인~아웃)
        6. 새 날짜 재고 차감
        7. Reservation 날짜/금액 업데이트*/

        OtaRequestLog otaRequestLog = otaRequestLogRepository.findByOtaReservationId(otaResId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        Reservation rv = otaRequestLog.getReservation();

        if (rv.getStatus() == Reservationstatus.PENDING || rv.getStatus() == Reservationstatus.CONFIRMED) {

            setRecoverRoomInventory(rv,rv.getRoomType());

            for (LocalDate date = rv.getCheckInDate(); date.isBefore(rv.getCheckOutDate()); date = date.plusDays(1)) {
                RoomDateInventory rdi = roomDateInventoryRepository
                        .findByRoomTypeAndDate(rv.getRoomType(), date)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                    rdi.book();

            }
            rv.modify(request.getCheckInDate(),request.getCheckOutDate(),request.getTotalPrice());
            return OtaReservationResponse.from(rv, otaRequestLog.getOtaReservationId());
        }else{
            throw new CustomException(ErrorCode.CAN_NOT_CHANGE_CONFIRMABLE_STATUS);
        }
    }

    public void delete(String otaResId) {
        OtaRequestLog otaRequestLog = otaRequestLogRepository.findByOtaReservationId(otaResId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        Reservation rv = otaRequestLog.getReservation();
        rv.cancel();
        setRecoverRoomInventory(rv,rv.getRoomType());
        setRecoverRoomOtaAllotment(rv,rv.getRoomType());
    }

    public OtaReservationResponse confirm(String otaResId) {

        OtaRequestLog otaRequestLog = otaRequestLogRepository.findByOtaReservationId(otaResId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        Reservation rv = otaRequestLog.getReservation();

        if (rv.getStatus() != Reservationstatus.PENDING) {
            throw new CustomException(ErrorCode.NOT_CONFIRMABLE_STATUS);
        }
        rv.confirm();
        return OtaReservationResponse.from(rv, otaRequestLog.getOtaReservationId());
    }

    public OtaReservationResponse createReservation(OtaReservationRequest request) {
        // 1. 멱등성 확인
        if (otaRequestLogRepository.existsByOtaChannelAndOtaReservationId(request.getOtaChannel(), request.getOtaReservationId())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESERVATION);
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
                .requestType(RequestType.CREATE)
                .build();
        otaRequestLogRepository.save(otaRequestLog);

        return OtaReservationResponse.from(rv, request.getOtaReservationId());

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

    private void setRecoverRoomInventory(Reservation rv, RoomType roomType) {
        for (LocalDate date = rv.getCheckInDate(); date.isBefore(rv.getCheckOutDate()); date = date.plusDays(1)) {
            RoomDateInventory rdi = roomDateInventoryRepository
                    .findByRoomTypeAndDate(roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            rdi.restore();
        }
    }

    private void setRecoverRoomOtaAllotment(Reservation rv, RoomType roomType) {
        for (LocalDate date = rv.getCheckInDate(); date.isBefore(rv.getCheckOutDate()); date = date.plusDays(1)) {
            OtaChannelAllotment ota = otaChannelAllotmentRepository
                    .findByOtaChannelAndRoomTypeAndDate(rv.getOtaChannel(), roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.ALLOTMENT_NOT_FOUND));

            ota.cancel();
        }
    }


}
