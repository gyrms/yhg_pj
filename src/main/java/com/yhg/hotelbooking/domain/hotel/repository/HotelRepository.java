package com.yhg.hotelbooking.domain.hotel.repository;


import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByAddress(String address);
}
