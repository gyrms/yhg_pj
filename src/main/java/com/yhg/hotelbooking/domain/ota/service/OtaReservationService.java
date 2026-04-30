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

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class OtaReservationService {

    private final OtaRequestLogRepository otaRequestLogRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomDateInventoryRepository roomDateInventoryRepository;
    private final OtaChannelAllotmentRepository otaChannelAllotmentRepository;
    private final ReservationRepository reservationRepository;
    private final RedissonClient redissonClient;

    public OtaReservationResponse update(String otaResId, OtaModifyRequest request) {

        OtaRequestLog otaRequestLog = otaRequestLogRepository.findByOtaReservationId(otaResId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        Reservation rv = otaRequestLog.getReservation();

        if (rv.getStatus() == Reservationstatus.CANCELLED) {
            throw new CustomException(ErrorCode.CAN_NOT_CHANGE_CONFIRMABLE_STATUS);
        }
        setRecoverRoomInventory(rv, rv.getRoomType());
        setRecoverRoomOtaAllotment(rv, rv.getRoomType());


        bookRoomInventory(request.getCheckInDate(), request.getCheckOutDate(), rv.getRoomType());
        bookOtaAllotment(request.getCheckInDate(), request.getCheckOutDate(), rv.getOtaChannel(), rv.getRoomType());

        rv.modify(request.getCheckInDate(), request.getCheckOutDate(), request.getTotalPrice());


        return OtaReservationResponse.from(rv, otaRequestLog.getOtaReservationId());

    }

    public void delete(String otaResId) {
        OtaRequestLog otaRequestLog = otaRequestLogRepository.findByOtaReservationId(otaResId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        Reservation rv = otaRequestLog.getReservation();
        rv.cancel();
        setRecoverRoomInventory(rv, rv.getRoomType());
        setRecoverRoomOtaAllotment(rv, rv.getRoomType());
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

        String lockKey = "lock:room:" + request.getRoomTypeId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 최대 5초 기다리고, 락 잡으면 10초 후 자동 해제
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            System.out.println(request.getOtaChannel() + request.getOtaReservationId()+"zzzzzzzzzzzzzzzzzzzz" + lockKey);
            if (!acquired) {
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

                OtaRequestLog oldReservation = otaRequestLogRepository.findByOtaChannelAndOtaReservationId(request.getOtaChannel(), request.getOtaReservationId()).orElse(null);

                if (oldReservation != null) {
                    return OtaReservationResponse.from(oldReservation.getReservation(), request.getOtaReservationId());
                }

                // 2. RoomType 조회
                RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                        .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

                bookRoomInventory(request.getCheckInDate(), request.getCheckOutDate(), roomType);
                bookOtaAllotment(request.getCheckInDate(), request.getCheckOutDate(), request.getOtaChannel(), roomType);

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

            redissonClient.getBucket("pending:reservation:" + rv.getId()).set("1", 600, TimeUnit.SECONDS);

                OtaRequestLog otaRequestLog = OtaRequestLog.builder()
                        .otaChannel(request.getOtaChannel())
                        .otaReservationId(request.getOtaReservationId())
                        .reservation(rv)
                        .requestType(RequestType.CREATE)
                        .build();
                otaRequestLogRepository.save(otaRequestLog);

                return OtaReservationResponse.from(rv, request.getOtaReservationId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
            } finally {
                // 내가 잡은 락이면 반드시 해제
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
    }

    private void bookRoomInventory(LocalDate checkin, LocalDate checkout, RoomType roomType) {
        for (LocalDate date = checkin; date.isBefore(checkout); date = date.plusDays(1)) {
            RoomDateInventory rdi = roomDateInventoryRepository
                    .findByRoomTypeAndDateWithLock(roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

            if (rdi.getAvailableCount() < 1) {          // 체크
                throw new CustomException(ErrorCode.INVENTORY_SOLD_OUT);
            }
            rdi.book();                                  // 차감 (같이)
        }
    }

    private void bookOtaAllotment(LocalDate checkin, LocalDate checkout, OtaChannel otaChannel, RoomType roomType) {

        for (LocalDate date = checkin; date.isBefore(checkout); date = date.plusDays(1)) {
            OtaChannelAllotment ota = otaChannelAllotmentRepository
                    .findByOtaChannelAndRoomTypeAndDateWithLock(otaChannel, roomType, date)
                    .orElseThrow(() -> new CustomException(ErrorCode.ALLOTMENT_NOT_FOUND));

            if (ota.getRemainingCount() < 1) {           // 체크
                throw new CustomException(ErrorCode.ALLOTMENT_EXHAUSTED);
            }
            ota.book();                                  // 차감 (같이)
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
