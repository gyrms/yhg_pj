package com.yhg.hotelbooking.domain.room.repository;

import com.yhg.hotelbooking.domain.room.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
}
