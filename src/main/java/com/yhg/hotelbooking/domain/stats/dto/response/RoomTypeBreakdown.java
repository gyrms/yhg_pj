package com.yhg.hotelbooking.domain.stats.dto.response;

import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomTypeBreakdown {

    private String grade;
    private long count;
    private long revenue;

    public static RoomTypeBreakdown from(String grade,List<Reservation> list) {
        return RoomTypeBreakdown.builder()
                .grade(grade)
                .count(list.size())
                .revenue(list.stream()
                        .filter(r -> r.getStatus() == Reservationstatus.CONFIRMED)
                        .mapToInt(Reservation::getTotalPrice).sum())

                .build();
    }
}
/*
RoomTypeBreakdown 리스트 생성

  ChannelBreakdown 만든 것과 완전히 똑같은 패턴이야.

  차이점은 채널 대신 객실 등급으로 필터링하는 것뿐이야.

  ---
  흐름

  전체 reservations 리스트
          ↓
  STANDARD만 필터링 → RoomTypeBreakdown 생성
  DELUXE만 필터링   → RoomTypeBreakdown 생성
  SUITE만 필터링    → RoomTypeBreakdown 생성
          ↓
  List<RoomTypeBreakdown> 완성

  ---
  먼저 RoomTypeBreakdown.from() 추가

  지금 RoomTypeBreakdown에 필드만 있잖아.
  ChannelBreakdown.from() 참고해서 똑같이 만들면 돼.

  from(grade 이름, 예약 리스트)
      → count = 리스트 사이즈
      → revenue = CONFIRMED만 totalPrice 합산

  ---
  StatsService에 추가할 코드 패턴

  ChannelBreakdown 만든 코드랑 비교해봐:

  // 채널별 — 이미 만든 것
  Arrays.stream(OtaChannel.values())
      .map(channel -> {
          List<Reservation> filtered = reservations.stream()
                  .filter(r -> r.getOtaChannel() == channel)  // 채널로 필터링
                  .collect(Collectors.toList());
          return ChannelBreakdown.from(channel.name(), filtered);
      })

  // 객실별 — 네가 만들 것
  Arrays.stream(RoomGrade.values())
      .map(grade -> {
          List<Reservation> filtered = reservations.stream()
                  .filter(r -> r.getRoomType().getGrade() == ???)  // 등급으로
  필터링
                  .collect(Collectors.toList());
          return RoomTypeBreakdown.from(???);
      })

  getOtaChannel() 대신 getRoomType().getGrade() 로 필터링하면 돼.

  ---
  1. RoomTypeBreakdown.from() 먼저 추가
  2. StatsService에 roomTypeBreakdown 리스트 생성 코드 추가
  3. DailyStatsResponse.builder() 에 .roomTypeBreakdown(roomTypeBreakdown) 추가

  해봐.

}*/
