package com.yhg.hotelbooking.domain.admin.service;

import com.yhg.hotelbooking.domain.Business.entity.BusinessDate;
import com.yhg.hotelbooking.domain.Business.repository.BusinessDateRepository;
import com.yhg.hotelbooking.domain.admin.dto.response.NightAuditResponse;
import com.yhg.hotelbooking.domain.allotment.repository.OtaChannelAllotmentRepository;
import com.yhg.hotelbooking.domain.inventory.repository.RoomDateInventoryRepository;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.global.config.CustomException;
import com.yhg.hotelbooking.global.config.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NightAuditService {

    private final ReservationRepository reservationRepository;
    private final RoomDateInventoryRepository roomDateInventoryRepository;
    private final OtaChannelAllotmentRepository otaChannelAllotmentRepository;
    private final BusinessDateRepository businessDateRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")  // 추가
    public NightAuditResponse findCheckoutDataToday() {
        BusinessDate businessDate = businessDateRepository.findById(1L)
                .orElseThrow(() -> new
                        CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        List<Reservation> rs =
                reservationRepository.findCheckoutDataToday(businessDate.getCurrentDate());

        for (Reservation reservation : rs) {
            reservation.noShow();
            for (LocalDate date = reservation.getCheckInDate();
                 date.isBefore(reservation.getCheckOutDate()); date = date.plusDays(1)) {

                roomDateInventoryRepository.findByRoomTypeAndDate(reservation.getRoomType(),
                        date).ifPresent(rdi -> rdi.restore());
                otaChannelAllotmentRepository.findByOtaChannelAndRoomTypeAndDate(reservation.getOtaChannel(), reservation.getRoomType(), date).ifPresent(ota
                        -> ota.cancel());
            }
        }

        // 영업일자 다음날로 전환
        businessDate.nextDay();

        List<NightAuditResponse.NoShowReservationResponse> noShowReservations
                = rs.stream()
                .map(reservation ->
                        NightAuditResponse.NoShowReservationResponse.builder()
                                .id(reservation.getId())
                                .guestName(reservation.getGuestName())
                                .checkInDate(reservation.getCheckInDate())
                                .build())
                .toList();

        return NightAuditResponse.builder()
                .processedCount(rs.size())
                .noShowReservations(noShowReservations)
                .build();
    }
}
