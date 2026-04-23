package com.yhg.hotelbooking.domain.allotment.entity;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "otachannelallotment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OtaChannelAllotment {

/*
    id
    ota_channel (Enum)
    room_type_id (FK)
    date
    allotted_count   ← 10 고정
    booked_count     ← 예약된 수
    remaining_count  ← 남은 수*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OtaChannel otaChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomtype;

    @Column(nullable = false)
    private LocalDate date;

    private Integer allottedCount;

    private Integer bookedCount;

    private Integer remainingCount;

    @Builder
    public OtaChannelAllotment(OtaChannel otaChannel,RoomType roomtype, LocalDate date) {
        this.otaChannel = otaChannel;
        this.roomtype = roomtype;
        this.date = date;
        this.allottedCount = 10;
        this.bookedCount = 0;
        this.remainingCount = 10;
    }

    public void book() {
        this.bookedCount++;
        this.remainingCount--;
    }

    public void cancel() {
        this.bookedCount--;
        this.remainingCount++;
    }

}
