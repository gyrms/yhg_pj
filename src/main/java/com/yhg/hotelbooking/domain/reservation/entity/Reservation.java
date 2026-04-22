package com.yhg.hotelbooking.domain.reservation.entity;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OtaChannel otaChannel;

    private String guestName;

    @Column(nullable = false)
    private String guestPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Reservationstatus status;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    private LocalDateTime lateArrival;

    private LocalDateTime earlyCheckout;

    @Column(nullable = false)
    private Integer totalPrice;
    private Integer nights;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Reservation(RoomType roomType, OtaChannel otaChannel, String guestName,
                       String guestPhone, LocalDate checkInDate, LocalDate checkOutDate,
                       LocalDateTime lateArrival,LocalDateTime earlyCheckout,
                       Integer totalPrice) {
        this.roomType = roomType;
        this.otaChannel = otaChannel;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.lateArrival = lateArrival;
        this.earlyCheckout = earlyCheckout;
        this.nights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        this.status = status.PENDING;  // 기본값                                         
    }

}

