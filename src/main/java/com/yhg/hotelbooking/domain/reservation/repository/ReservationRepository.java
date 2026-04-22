package com.yhg.hotelbooking.domain.reservation.repository;


import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "JOIN FETCH r.roomType rt " +
            "WHERE r.id = :rsId " +
            "AND r.status = 'CONFIRMED'")
    Optional<Reservation> findConfirmedById(@Param("rsId") Long rsId);

}
