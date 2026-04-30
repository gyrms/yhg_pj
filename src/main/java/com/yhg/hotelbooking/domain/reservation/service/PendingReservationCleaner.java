package com.yhg.hotelbooking.domain.reservation.service;

import com.yhg.hotelbooking.domain.allotment.repository.OtaChannelAllotmentRepository;
import com.yhg.hotelbooking.domain.inventory.repository.RoomDateInventoryRepository;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingReservationCleaner {

    private final ReservationRepository reservationRepository;
    private final RoomDateInventoryRepository roomDateInventoryRepository;
    private final OtaChannelAllotmentRepository otaChannelAllotmentRepository;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    @Transactional
    public void recoverExpiredPendingReservations() {

        // 생성된 지 600초(10분) 이상 지났는데 아직 PENDING인 예약 조회
        LocalDateTime expiredBefore = LocalDateTime.now().minusSeconds(600);

        List<Reservation> expiredList = reservationRepository
                .findByStatusAndCreatedAtBefore(Reservationstatus.PENDING, expiredBefore);

        if (expiredList.isEmpty()) {
            return;
        }

        log.info("[PendingCleaner] 만료 예약 {}건 복구 시작", expiredList.size());

        for (Reservation rv : expiredList) {

            // 체크인 ~ 체크아웃 날짜별로 재고 복구
            for (LocalDate date = rv.getCheckInDate(); date.isBefore(rv.getCheckOutDate()); date
                    = date.plusDays(1)) {

                // 실제 재고 복구
                roomDateInventoryRepository
                        .findByRoomTypeAndDate(rv.getRoomType(), date)
                        .ifPresent(rdi -> rdi.restore());

                // OTA 채널 할당 재고 복구
                otaChannelAllotmentRepository
                        .findByOtaChannelAndRoomTypeAndDate(rv.getOtaChannel(), rv.getRoomType(),
                                date)
                        .ifPresent(ota -> ota.cancel());
            }

            rv.expire(); // status → EXPIRED
            log.info("[PendingCleaner] 예약 ID {} 만료 처리 완료", rv.getId());
        }
    }
}
