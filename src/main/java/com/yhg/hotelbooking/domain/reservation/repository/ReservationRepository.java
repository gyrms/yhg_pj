package com.yhg.hotelbooking.domain.reservation.repository;


import com.yhg.hotelbooking.domain.hotel.entity.Hotel;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "JOIN FETCH r.roomType rt " +
            "WHERE r.id = :rsId " +
            "AND r.status = 'CONFIRMED'")
    Optional<Reservation> findConfirmedById(@Param("rsId") Long rsId);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "WHERE r.checkInDate = :date "+
            "AND r.status = 'CONFIRMED' " +
            "AND r.lateArrival = false")
    List<Reservation> findCheckoutDataToday(@Param("date") LocalDate date);

    List<Reservation> findByStatusAndCreatedAtBefore(  Reservationstatus status,  LocalDateTime dateTime );
}
