package com.yhg.hotelbooking.domain.inventory.entity;

import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import com.yhg.hotelbooking.domain.room.entity.RoomGrade;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "roomdateinventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RoomDateInventory {
  /*  id
    room_type_id (FK)
    date
    total_count      ← 21 고정
    booked_count     ← 예약된 수
    available_count  ← 남은 수 (total - booked)
    version          ← 낙관적 락용 (Phase 3)*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    private LocalDate date;

    private Integer totalCount;

    private Integer bookedCount;
    private Integer availableCount;
    @Version
    private Integer version;

    @Builder
    public RoomDateInventory(RoomType roomType, LocalDate date) {
        this.roomType = roomType;
        this.date = date;
        this.totalCount = roomType.getTotalCount();
        this.bookedCount = 0;
        this.availableCount = roomType.getTotalCount();
    }

    public void book() {
        this.bookedCount++;
        this.availableCount--;
    }

    public void restore() {
        this.bookedCount--;
        this.availableCount++;
    }


}
