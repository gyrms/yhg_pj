package com.yhg.hotelbooking.domain.stats.dto.response;

import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChannelBreakdown {

    private String channel;
    private long count;
    private double revenue;

    public static ChannelBreakdown from(String channelName ,List<Reservation> list) {
        return  ChannelBreakdown.builder()
                .channel(channelName)
                .count(list.size())
                .revenue(list.stream().filter(r-> r.getStatus()==Reservationstatus.CONFIRMED)
                        .mapToInt(Reservation::getTotalPrice).sum())
                .build();
    }
}


/*

좋아. 근데 revenue 계산에 한 가지 빠진 게 있어.

        ---
지금 이렇게 돼 있어:
        .revenue(list.stream().mapToInt(Reservation::getTotalPrice).sum())

        이러면 CANCELLED, PENDING 된 예약 매출까지 다 합산돼.

매출은 CONFIRMED 된 것만 합산해야 해:

        .revenue(list.stream()
          .filter(r -> r.getStatus() == Reservationstatus.CONFIRMED)
        .mapToInt(Reservation::getTotalPrice).sum())

        StatusBreakdown.from() 만들 때 썼던 패턴이랑 똑같아.
filter 한 줄만 추가해봐.
*/
