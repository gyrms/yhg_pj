package com.yhg.hotelbooking.domain.stats.service;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.domain.room.entity.RoomGrade;
import com.yhg.hotelbooking.domain.stats.dto.response.ChannelBreakdown;
import com.yhg.hotelbooking.domain.stats.dto.response.DailyStatsResponse;
import com.yhg.hotelbooking.domain.stats.dto.response.RoomTypeBreakdown;
import com.yhg.hotelbooking.domain.stats.dto.response.StatusBreakdown;
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

}
