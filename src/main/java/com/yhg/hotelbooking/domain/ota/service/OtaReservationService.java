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
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
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

        OtaRequestLog otaRequestLog = otaRequestLogRepository.findByOtaReservationId(otaResId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        Reservation rv = otaRequestLog.getReservation();

        if (rv.getStatus() == Reservationstatus.CANCELLED) {
            throw new CustomException(ErrorCode.CAN_NOT_CHANGE_CONFIRMABLE_STATUS);
        }
            setRecoverRoomInventory(rv,rv.getRoomType());
            setRecoverRoomOtaAllotment(rv,rv.getRoomType());



        checkRoomInventory(request.getCheckInDate(),request.getCheckOutDate(), rv.getRoomType(), false);
        checkOtaAllotment(request.getCheckInDate(),request.getCheckOutDate(), rv.getOtaChannel(),rv.getRoomType(),false);

        checkRoomInventory(request.getCheckInDate(),request.getCheckOutDate(), rv.getRoomType(), true);
        checkOtaAllotment(request.getCheckInDate(),request.getCheckOutDate(), rv.getOtaChannel(),rv.getRoomType(), true);
            rv.modify(request.getCheckInDate(),request.getCheckOutDate(),request.getTotalPrice());


            return OtaReservationResponse.from(rv, otaRequestLog.getOtaReservationId());

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
        /*if (otaRequestLogRepository.existsByOtaChannelAndOtaReservationId(request.getOtaChannel(), request.getOtaReservationId())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESERVATION);
        }*/

        OtaRequestLog OldReservation =  otaRequestLogRepository.existsByOtaChannelAndOtaReservationId(request.getOtaChannel(), request.getOtaReservationId()).orElse(null);

        if(OldReservation != null){
            return OtaReservationResponse.from(OldReservation.getReservation(), request.getOtaReservationId());
        }

        // 2. RoomType 조회
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        checkRoomInventory(request.getCheckInDate(),request.getCheckOutDate(), roomType, false);
        checkOtaAllotment(request.getCheckInDate(),request.getCheckOutDate(), request.getOtaChannel(),roomType,false);

        checkRoomInventory(request.getCheckInDate(),request.getCheckOutDate(), roomType, true);
        checkOtaAllotment(request.getCheckInDate(),request.getCheckOutDate(), request.getOtaChannel(),roomType,true);

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

    private void checkRoomInventory(LocalDate checkin,LocalDate checkout, RoomType roomType, boolean isplay) {

        // 5. 실제 재고 차감
        for (LocalDate date = checkin; date.isBefore(checkout); date = date.plusDays(1)) {
            RoomDateInventory rdi = roomDateInventoryRepository
                    .findByRoomTypeAndDate(roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_SOLD_OUT));
            if (isplay) {
                rdi.book();
            } else {
                if (rdi.getAvailableCount() < 1) {
                    throw new CustomException(ErrorCode.INVENTORY_SOLD_OUT);
                }
            }
        }
    }

    private void checkOtaAllotment(LocalDate checkin, LocalDate checkout, OtaChannel otaChannel, RoomType roomType, boolean isplay) {

        for (LocalDate date = checkin; date.isBefore(checkout); date = date.plusDays(1)) {
            OtaChannelAllotment ota = otaChannelAllotmentRepository
                    .findByOtaChannelAndRoomTypeAndDate(otaChannel, roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.ALLOTMENT_EXHAUSTED));
            if (isplay) {
                ota.book();
            } else {
                if (ota.getRemainingCount() < 1) {
                    throw new CustomException(ErrorCode.ALLOTMENT_EXHAUSTED);
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
