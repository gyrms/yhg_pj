package com.yhg.hotelbooking.domain.dashboard.dto.response;

import com.yhg.hotelbooking.domain.reservation.dto.response.ReservationResponse;
import com.yhg.hotelbooking.domain.reservation.entity.Reservation;
import com.yhg.hotelbooking.domain.reservation.entity.Reservationstatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {
    private long totalRoomTypes;
    private ReservationSummary reservationSummary;
    private List<ReservationResponse> reservations;
/*

    public static DashboardResponse from(List<Reservation> all, long totalRoomTypes ) {
        return new DashboardResponse(all,totalRoomTypes);
    }

    private DashboardResponse(List<Reservation> all, long totalRoomTypes ) {
        this.totalRoomTypes = totalRoomTypes;
        this.reservationSummary = new ReservationSummary(
                all.size(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.PENDING).count(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.CONFIRMED).count(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.CHECKED_IN).count(),
                all.stream().filter(r -> r.getStatus() == Reservationstatus.CANCELLED).count()
        );
        this.reservations = all.stream().map(ReservationResponse::from).collect(java.util.stream.Collectors.toList());

    }
*/



}

/*
from()에 뭘 넣을지는 서비스에서 뭘 넘겨줄 건지 생각하면 돼.

서비스 코드 보면:
List<Reservation> all = reservationRepository.findAll();
int totalRoomTypes = roomTypeRepository.count();

이 두 개를 from()에 넘겨주면 되겠지?

public static DashboardResponse from(List<Reservation> all, int totalRoomTypes)

그럼 생성자 안에서 all을 가지고 뭘 계산할 수 있어?

        - all.size() → total 예약 수
  - all.stream().filter(...).count() → 상태별 카운트
  - all.stream().map(ReservationResponse::from)... → 예약 목록

ReservationSummary도 마찬가지야. 얘도 필드에 값을 넣는 방법이 필요해. DashboardResponse 먼저
완성해봐.

  this.totalRoomTypes = totalRoomTypes;
          this.total = all.size();
          this.pending = all.stream().filter(r -> r.getStatus() == Reservationstatus.PENDING).count();
          this.confirmed = all.stream().filter(r -> r.getStatus() ==
  Reservationstatus.CONFIRMED).count();
          this.checkedIn = all.stream().filter(r -> r.getStatus() ==
  Reservationstatus.CHECKED_IN).count();
          this.cancelled = all.stream().filter(r -> r.getStatus() ==
  Reservationstatus.CANCELLED).count();
          this.reservations =
  all.stream().map(ReservationResponse::from).collect(Collectors.toList());
● ReservationSummary도 똑같이 생성자나 빌더로 값을 채워야 해.

  ReservationSummary 파일 열어보면 필드만 있잖아. 거기에 먼저 값 넣는 방법을 추가해봐.

  DashboardResponse 생성자에서 all을 가지고 카운트 계산하고, 그 값들을 ReservationSummary에 넘겨주는
  방식이야.

  ReservationSummary를 어떻게 만들면 될지 생각해봐. 지금 DashboardResponse 생성자에서 하듯이
  (파라미터)로 값 받아서 this.xxx = xxx 세팅하는 거야.

  ReservationSummary 먼저 완성해봐.
 */

