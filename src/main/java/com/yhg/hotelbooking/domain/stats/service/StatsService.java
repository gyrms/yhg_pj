package com.yhg.hotelbooking.domain.stats.service;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.domain.room.entity.RoomGrade;
import com.yhg.hotelbooking.domain.stats.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    public final ReservationRepository reservationRepository;

    public DailyStatsResponse getDailyStats(LocalDate date) {
        List<Reservation> reservations= reservationRepository.findByCheckInDate(date);

        int totalRevenue = reservations.stream()
                .mapToInt(Reservation::getTotalPrice)
                .sum();
        int totalReservations = reservations.size();

        List<ChannelBreakdown> channelBreakdown = Arrays.stream(OtaChannel.values())
            .map(channel -> {
                List<Reservation> filtered = reservations.stream()
                        .filter(r -> r.getOtaChannel() == channel)
                        .collect(Collectors.toList());
                return ChannelBreakdown.from(channel.name(), filtered);
            })
            .collect(Collectors.toList());

        List<RoomTypeBreakdown> roomTypeBreakdown = Arrays.stream(RoomGrade.values())
            .map(grade -> {
                List<Reservation> filtered = reservations.stream()
                        .filter(r -> r.getRoomType().getGrade() == grade)
                        .collect(Collectors.toList());
                return RoomTypeBreakdown.from(grade.name(), filtered);
            })
            .collect(Collectors.toList());

        return DailyStatsResponse.builder()
                .date(date.toString())
                .totalRevenue(totalRevenue)
                .totalReservations(totalReservations)
                .statusBreakdown(StatusBreakdown.from(reservations))
                .channelBreakdown(channelBreakdown)
                .roomTypeBreakdown(roomTypeBreakdown)
                .build();
    }

    public ChannelStatsResponse getChannelStats(OtaChannel channelName,LocalDate date) {
        List<Reservation> reservations= reservationRepository.findByOtaChannelAndCheckInDate(channelName,date);

        int totalRevenue = reservations.stream().filter(
                r -> r.getStatus() == Reservationstatus.CONFIRMED
                )
                .mapToInt(Reservation::getTotalPrice)
                .sum();
        double avgNights = reservations.stream().mapToInt(Reservation::getNights).average().orElse(0);

        List<RoomTypeBreakdown> roomTypeBreakdown = Arrays.stream(RoomGrade.values())
                .map(grade -> {
                    List<Reservation> filtered = reservations.stream()
                            .filter(r -> r.getRoomType().getGrade() == grade)
                            .collect(Collectors.toList());
                    return RoomTypeBreakdown.from(grade.name(), filtered);
                })
                .collect(Collectors.toList());

        return ChannelStatsResponse.builder()
                .channel(channelName.name())
                .date(date.toString())
                .totalRevenue(totalRevenue)
                .totalReservations(reservations.size())
                .avgNights(avgNights)
                .statusBreakdown(StatusBreakdown.from(reservations))
                .roomTypeBreakdown(roomTypeBreakdown)
                .build();
    }

}
