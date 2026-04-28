package com.yhg.hotelbooking.concurrency;

import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.inventory.repository.RoomDateInventoryRepository;
import com.yhg.hotelbooking.domain.ota.dto.request.OtaReservationRequest;
import com.yhg.hotelbooking.domain.ota.service.OtaReservationService;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import com.yhg.hotelbooking.domain.room.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private OtaReservationService otaReservationService;
    @Autowired
    private RoomDateInventoryRepository roomDateInventoryRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Test
    @org.springframework.transaction.annotation.Transactional
    void 동시_예약_30개_테스트() throws InterruptedException {
        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        OtaChannel[] channels = {OtaChannel.YANOLJA, OtaChannel.YEOGI,OtaChannel.YOGISEO};
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            final OtaChannel channel = channels[idx % 3];  // 0,1,2,0,1,2... 순서로

            executorService.submit(() -> {
                try {
                    // 예약 요청 생성 후 createReservation 호출
                    OtaReservationRequest request = OtaReservationRequest.builder()
                            .otaChannel(channel)           // ← 채널 3개 번갈아가며
                            .otaReservationId("reservation" + idx)
                            .roomTypeId(1L)
                            .guestName("guest" + idx)
                            .guestPhone("010-1234-5678")
                            .checkInDate(LocalDate.now().plusDays(1))
                            .checkOutDate(LocalDate.now().plusDays(2))
                            .totalPrice(10000)
                            .build();
                    System.out.println(request.toString());
                    otaReservationService.createReservation(request);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        // DB에서 해당 날짜 available_count 확인
        // 30개 요청인데 실제 재고보다 많이 차감됐는지 확인
        RoomType roomType = roomTypeRepository.findById(1L).orElseThrow();

        RoomDateInventory result = roomDateInventoryRepository
                .findByRoomTypeAndDate(roomType, LocalDate.now().plusDays(1))
                .orElseThrow();
        System.out.println("=== 결과 ===");
        System.out.println("available_count: " + result.getAvailableCount());
    }
}
/*

CountDownLatch = 출발 신호 대기30분 언제끝 wsdhknm마라톤 출발선에 30명이 대기하고 있다가 총소리 한 번에 동시에 출발하는 것과 같아.

        30개 스레드 생성 → 전부 대기 → latch.countDown() → 동시 출발

  ---
코드로 보면:

CountDownLatch latch = new CountDownLatch(30); // 30명 대기

  for (int i = 0; i < 30; i++) {
        executorService.submit(() -> {
        try {
        // 예약 요청 실행
        } finally {
        latch.countDown(); // 1명 완료 신호
          }
                  });
                  }

                  latch.await(); // 30명 전부 완료될 때까지 기다림

  - new CountDownLatch(30) → 30 카운트 세팅
  - latch.countDown() → 카운트 1 감소
  - latch.await() → 카운트가 0 될 때까지 메인 스레드 대기

  ---
이해됐으면 테스트 코드 만들어봐.    */
