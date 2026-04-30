package com.yhg.hotelbooking.domain.stats.dto.response;

import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static org.antlr.v4.runtime.misc.Utils.count;

@Getter
@Builder
public class StatusBreakdown {
/*      "confirmed": 12,
        "pending": 3,
        "cancelled": 5,
        "checkedIn": 2,
        "noShow": 1,
        "expired": 7*/

    private long confirmed;
    private long pending;
    private long cancelled;
    private long checkedIn;
    private long noShow;
    private long expired;

    public static StatusBreakdown from(List<Reservation> list) {
        return StatusBreakdown.builder()
                .confirmed(count(list, Reservationstatus.CONFIRMED))
                .pending(count(list, Reservationstatus.PENDING))
                .cancelled(count(list, Reservationstatus.CANCELLED))
                .checkedIn(count(list, Reservationstatus.CHECKED_IN))
                .noShow(count(list, Reservationstatus.NO_SHOW))
                .expired(count(list, Reservationstatus.EXPIRED))
                .build();
    }
    private static long count(List<Reservation> list, Reservationstatus status)
    {
        return list.stream().filter(r -> r.getStatus() == status).count();
    }

}
