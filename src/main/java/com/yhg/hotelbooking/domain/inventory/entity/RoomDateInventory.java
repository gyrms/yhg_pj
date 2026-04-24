package com.yhg.hotelbooking.domain.inventory.entity;

import com.yhg.hotelbooking.domain.room.entity.RoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "roomdateinventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RoomDateInventory {

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
