package com.yhg.hotelbooking.domain.room.entity;

import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomGrade grade;

    @Column(nullable = false)
    private Integer basePrice;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer totalCount;// STANDARD, DELUXE, SUITE

    @Builder
    public RoomType(Hotel hotel, String name, RoomGrade grade,
                    Integer basePrice, Integer capacity, Integer totalCount) {
        this.hotel = hotel;
        this.name = name;
        this.grade = grade;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.totalCount = totalCount;
    }
}
