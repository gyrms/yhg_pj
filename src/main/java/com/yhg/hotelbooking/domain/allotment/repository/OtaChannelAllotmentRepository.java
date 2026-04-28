package com.yhg.hotelbooking.domain.allotment.repository;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.QueryHint;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;


public interface OtaChannelAllotmentRepository extends JpaRepository<OtaChannelAllotment, Long> {
    // 락 없는 일반 조회 (복구, 결과 확인용)
    Optional<OtaChannelAllotment> findByOtaChannelAndRoomTypeAndDate(
            OtaChannel otaChannel, RoomType roomType, LocalDate date);

    // 락 있는 조회 (예약 처리 전용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "10000"))
    @Query("SELECT o FROM OtaChannelAllotment o WHERE o.otaChannel = :otaChannel AND o.roomType = :roomType AND o.date = :date")
    Optional<OtaChannelAllotment> findByOtaChannelAndRoomTypeAndDateWithLock(
            @Param("otaChannel") OtaChannel otaChannel,
            @Param("roomType") RoomType roomType,
            @Param("date") LocalDate date);
}

