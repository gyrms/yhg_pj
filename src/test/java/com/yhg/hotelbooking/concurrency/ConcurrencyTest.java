package com.yhg.hotelbooking.concurrency;

import com.yhg.hotelbooking.domain.allotment.entity.OtaChannelAllotment;
import com.yhg.hotelbooking.domain.allotment.repository.OtaChannelAllotmentRepository;
import com.yhg.hotelbooking.domain.inventory.entity.RoomDateInventory;
import com.yhg.hotelbooking.domain.inventory.repository.RoomDateInventoryRepository;
import com.yhg.hotelbooking.domain.ota.dto.request.OtaReservationRequest;
import com.yhg.hotelbooking.domain.ota.repository.OtaRequestLogRepository;
import com.yhg.hotelbooking.domain.ota.service.OtaReservationService;
import com.yhg.hotelbooking.domain.otachannel.entity.OtaChannel;
import com.yhg.hotelbooking.domain.reservation.repository.ReservationRepository;
import com.yhg.hotelbooking.domain.room.entity.RoomType;
import com.yhg.hotelbooking.domain.room.repository.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private OtaReservationService otaReservationService;
    @Autowired
    private RoomDateInventoryRepository roomDateInventoryRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private OtaRequestLogRepository otaRequestLogRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private OtaChannelAllotmentRepository otaChannelAllotmentRepository;


    @Test
    void 동시_예약_30개_테스트() throws InterruptedException {
        int threadCount = 30;
        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        OtaChannel[] channels = {OtaChannel.YANOLJA, OtaChannel.YEOGI, OtaChannel.YOGISEO};

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            final OtaChannel channel = channels[idx % 3];

            executorService.submit(() -> {
                try {
                    OtaReservationRequest request =
                            OtaReservationRequest.builder()
                                    .otaChannel(channel)
                                    .otaReservationId("reservation" + idx + "-" + UUID.randomUUID())
                                    .roomTypeId(1L)
                                    .guestName("guest" + idx)
                                    .guestPhone("010-1234-5678")
                                    .checkInDate(LocalDate.now().plusDays(1))
                                    .checkOutDate(LocalDate.now().plusDays(2))
                                    .totalPrice(10000)
                                    .build();
                    otaReservationService.createReservation(request);
                    success.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("실패: " + e.getMessage());
                    failure.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 새 트랜잭션으로 결과 조회
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        Integer availableCount = txTemplate.execute(status -> {
            RoomType roomType = roomTypeRepository.findById(1L).orElseThrow();
            return roomDateInventoryRepository
                    .findByRoomTypeAndDate(roomType,
                            LocalDate.now().plusDays(1))
                    .orElseThrow()
                    .getAvailableCount();
        });

        System.out.println("===== 결과 =====");
        System.out.println("성공: " + success.get());
        System.out.println("실패: " + failure.get());
        System.out.println("available_count: " + availableCount);
    }

    @BeforeEach
    void setUp() {
   /*     otaRequestLogRepository.deleteAll();
        reservationRepository.deleteAll();*/
        otaRequestLogRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();

        List<RoomDateInventory> inventories = roomDateInventoryRepository.findAll();
        inventories.forEach(RoomDateInventory::reset);
        roomDateInventoryRepository.saveAll(inventories);

        List<OtaChannelAllotment> allotments = otaChannelAllotmentRepository.findAll();
        allotments.forEach(OtaChannelAllotment::reset);
        otaChannelAllotmentRepository.saveAll(allotments);

        // 초기 상태 확인
        roomDateInventoryRepository.findAll()
                .forEach(rdi -> System.out.println(
                        "inventory - date: " + rdi.getDate() +
                                ", total: " + rdi.getTotalCount() +
                                ", available: " + rdi.getAvailableCount()));

        otaChannelAllotmentRepository.findAll()
                .forEach(ota -> System.out.println(
                        "allotment - channel: " + ota.getOtaChannel() +
                                ", date: " + ota.getDate() +
                                ", allotted: " + ota.getAllottedCount() +
                                ", remaining: " + ota.getRemainingCount()));
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
