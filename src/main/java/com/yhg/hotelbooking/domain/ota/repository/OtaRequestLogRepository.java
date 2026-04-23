package com.yhg.hotelbooking.domain.ota.repository;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.ota.entity.OtaRequestLog;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface OtaRequestLogRepository extends JpaRepository<OtaRequestLog, Long> {
    boolean existsByOtaChannelAndOtaReservationId(
            OtaChannel otaChannel, String otaReservationId
    );

    Optional<OtaRequestLog> findByOtaReservationId(String otaReservationId);
}