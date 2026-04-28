package com.yhg.hotelbooking.domain.inventory.repository;

import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDate;
import java.util.Optional;

public interface RoomDateInventoryRepository extends JpaRepository<RoomDateInventory, Long> {

    Optional<RoomDateInventory> findByRoomTypeAndDate(RoomType roomType, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value =
            "3000"))
    Optional<RoomDateInventory> findByRoomTypeAndDateForUpdate(RoomType roomType,
                                                               LocalDate date);

}
