package com.yhg.hotelbooking.domain.ota.repository;

import com.yhg.hotelbooking.domain.ota.entity.OtaRequestLog;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtaRequestLogRepository extends JpaRepository<OtaRequestLog, Long> {
    Optional<OtaRequestLog> findByOtaReservationId(String otaReservationId);

    Optional<OtaRequestLog> findByOtaChannelAndOtaReservationId(OtaChannel otaChannel, String otaReservationId);
}