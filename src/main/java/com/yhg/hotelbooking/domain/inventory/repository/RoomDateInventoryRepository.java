package com.yhg.hotelbooking.domain.inventory.repository;

import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RoomDateInventoryRepository extends JpaRepository<RoomDateInventory, Long> {
    Optional<RoomDateInventory> findByRoomTypeAndDate(RoomType roomType, LocalDate date);
}
