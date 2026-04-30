package com.yhg.hotelbooking.domain.stats.controller;

import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.stats.dto.response.ChannelStatsResponse;
import com.yhg.hotelbooking.domain.stats.dto.response.DailyStatsResponse;
import com.yhg.hotelbooking.domain.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/daily")
    public ResponseEntity<DailyStatsResponse> getDailyStats( @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DailyStatsResponse response = statsService.getDailyStats(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<ChannelStatsResponse> getChannelStats(@PathVariable OtaChannel channelName,   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        ChannelStatsResponse response = statsService.getChannelStats(channelName,date);
        return ResponseEntity.ok(response);
    }
}

