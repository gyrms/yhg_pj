package com.yhg.hotelbooking.domain.ota.entity;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "otarequestlog",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"ota_channel", "ota_reservation_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OtaRequestLog {

    /*id
    ota_channel
    ota_reservation_id   ← OTA 측 예약 ID
    request_type         ← CREATE / MODIFY / CANCEL
    processed_at*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OtaChannel otaChannel;

    @Column(nullable = false)
    private String otaReservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestType requestType;

    @CreationTimestamp
    private LocalDateTime processedAt;

    @Builder
    public OtaRequestLog(OtaChannel otaChannel, String otaReservationId,
                         Reservation reservation,RequestType requestType) {
        this.otaChannel = otaChannel;
        this.otaReservationId = otaReservationId;
        this.reservation = reservation;
        this.requestType = requestType;
    }



}



