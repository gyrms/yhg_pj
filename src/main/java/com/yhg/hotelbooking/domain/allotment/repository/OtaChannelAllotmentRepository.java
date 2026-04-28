package com.yhg.hotelbooking.domain.allotment.repository;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.QueryHint;

import java.time.LocalDate;
import java.util.Optional;


public interface OtaChannelAllotmentRepository extends JpaRepository<OtaChannelAllotment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value ="3000"))
    Optional<OtaChannelAllotment> findByOtaChannelAndRoomTypeAndDate(
            OtaChannel otaChannel, RoomType roomType, LocalDate date
    );
}

