package com.yhg.hotelbooking.domain.allotment.repository;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface OtaChannelAllotmentRepository extends JpaRepository<OtaChannelAllotment, Long> {
    Optional<OtaChannelAllotment> findByOtaChannelAndRoomTypeAndDate(
            OtaChannel otaChannel, RoomType roomType, LocalDate date
    );
}
