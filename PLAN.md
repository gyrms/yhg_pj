# 호텔 예약 시스템 (OTA 연동) - 1차 프로젝트 계획서

> 작성일: 2026-04-15 | 프로젝트명: hotel-reservation

---

## 1. 프로젝트 개요

**목적**
- 다중 OTA(야놀자/여기어때/요기서)를 통한 호텔 객실 예약 시스템 구현
- OTA별 할당 재고 + 공용 재고의 동시 예약 정합성 보장 (핵심 기술 과제)
- 실무(CMS팀 OTA 연동 업무) 경험을 바탕으로 한 사이드 프로젝트

**등장인물**
| 구분 | 내용 |
|---|---|
| OTA 3개 | 야놀자(YANOLJA), 여기어때(YEOGI), 요기서(YOGISEO) → Mock 서버로 구현 |
| 호텔 1개 | 윤효근 호텔 (실제 객실 21개 보유) |
| 채널 매니저 | 본 시스템이 담당 (OTA ↔ 호텔 재고 중계) |

---

## 2. 핵심 비즈니스 규칙

### OTA 채널별 할당 재고 구조 ★ 이 프로젝트의 핵심 특징

```
실제 객실: 21개 (윤효근 호텔 전체 보유량)

OTA별 판매 할당량:
  야놀자   : 최대 10개
  여기어때 : 최대 10개
  요기서   : 최대 10개
  할당 합계: 30개 → 실제보다 많음 (초과 할당)
```

**재고 차감 규칙**

OTA가 예약 요청을 보내면 두 가지를 동시에 검사한다.
- 조건 A: 해당 OTA의 남은 할당량이 1개 이상인가?
- 조건 B: 호텔의 실제 남은 객실이 1개 이상인가?
- → **둘 다 충족할 때만 예약 성공, 둘 다 차감**

**예시 (5월 1일 기준)**

| 시점 | 호텔 실제 재고 | 야놀자 잔여 | 여기어때 잔여 | 요기서 잔여 |
|---|---|---|---|---|
| 초기 | 21 | 10 | 10 | 10 |
| 야놀자 8개 예약 후 | 13 | 2 | 10 | 10 |
| 여기어때 10개 시도 | 3만 성공 (실제 재고 병목) | 2 | 7 | 10 |
| 야놀자 할당 소진 후 추가 시도 | 실패 (할당 0) | 0 | 7 | 10 |

### 장기 숙박 재고 처리
- 재고는 날짜별로 관리
- 2박 이상 예약 시 체크인일 ~ 체크아웃 전날까지 각 날짜 재고 차감
  - 예) 5월 1일~3일(2박) → 5월 1일, 5월 2일 각각 차감
- 조기 퇴실 시 남은 날짜 재고 즉시 복구

### 일마감 (Night Audit)
- 하루 1회 야간에 일마감 처리
- 처리 항목:
  1. 당일 체크인 예정이었으나 미도착 예약 → 자동 NO_SHOW 전환 (late_arrival=true 제외)
  2. 영업일자(Business Date)를 다음날로 전환
  3. 일마감 보고서 생성

### 예약 상태 (State Machine)

```
PENDING ──────────────────────────────▶ CANCELLED
   │                                        ▲
   ▼                                        │
CONFIRMED ──────────────────────────────────┤
   │                                        │
   ▼                                        │
CHECKED_IN ──────────────────────────────────┤
   │                       │               │
   ▼                       ▼               │
CHECKED_OUT            NO_SHOW ───────────▶┘
                       (야간감사 자동)
```

| 상태 | 설명 |
|---|---|
| PENDING | OTA 요청 수신 직후, 임시 선점 (TTL 10분) |
| CONFIRMED | 예약 확정 |
| CHECKED_IN | 체크인 완료, 투숙 중 |
| CHECKED_OUT | 체크아웃 완료 |
| NO_SHOW | 노쇼 (야간감사 자동 전환) |
| CANCELLED | 취소 |

**특별 플래그**
- `late_arrival = true` → 야간감사 시 NO_SHOW 자동전환 제외
- `early_checkout = true` → 조기 퇴실 처리

### 체크인/체크아웃
- 기본 체크인: 15:00
- 기본 체크아웃: 11:00
- 레이트 체크인: 별도 요금 없음 (late_arrival 플래그 등록)
- 얼리 체크인: 객실 상황에 따라 처리

---

## 3. 도메인 설계 (ERD 요약)

```
hotel
  id, name, address, description

room_type
  id, hotel_id, name, grade(STANDARD/DELUXE/SUITE)
  base_price, capacity, total_count(=21)

room_date_inventory  ← 날짜별 실제 재고 ★
  id, room_type_id, date
  total_count(21), booked_count, available_count
  version (낙관적 락용)

ota_channel_allotment  ← OTA별 날짜별 할당 재고 ★
  id, ota_channel(YANOLJA/YEOGI/YOGISEO), room_type_id, date
  allotted_count(10), booked_count, remaining_count

reservation
  id, room_type_id, ota_channel
  guest_name, guest_phone
  check_in_date, check_out_date, nights
  status, late_arrival, early_checkout
  total_price, created_at

ota_request_log  ← 멱등성 처리용
  id, ota_channel, ota_reservation_id, request_type, processed_at
  UNIQUE KEY (ota_channel, ota_reservation_id)
```

---

## 4. API 목록

### 호텔/객실 조회 (공개)
```
GET  /api/hotels
GET  /api/hotels/{id}/room-types
GET  /api/room-types/{id}/availability?checkIn=&checkOut=
```

### 예약 관리 (ADMIN)
```
GET    /api/reservations
GET    /api/reservations/{id}
POST   /api/reservations/{id}/checkin
POST   /api/reservations/{id}/checkout
DELETE /api/reservations/{id}
```

### OTA 연동 (OTA Mock → 본 시스템)
```
POST   /api/ota/reservations               - OTA 예약 요청 수신
PUT    /api/ota/reservations/{otaResId}    - OTA 예약 수정 수신
DELETE /api/ota/reservations/{otaResId}    - OTA 예약 취소 수신
POST   /api/ota/reservations/{otaResId}/confirm - 예약 확정
```

**POST /api/ota/reservations 처리 흐름**
```
멱등성 확인 → 실제 재고 확인 → OTA 할당 재고 확인 → 둘 다 차감 → PENDING 저장
응답: 성공 / 재고부족(실제) / 할당소진(OTA) 구분
```

### 현황 조회 (ADMIN) ★
```
GET  /api/dashboard                          - 전체 현황 요약
GET  /api/room-types/status?date=2026-05-01  - 룸타입별 당일 재고 현황
```

**GET /api/dashboard 응답 예시**
```json
{
  "totalRoomTypes": 3,
  "reservationSummary": {
    "total": 10,
    "PENDING": 3,
    "CONFIRMED": 5,
    "CHECKED_IN": 2,
    "CANCELLED": 0
  },
  "reservations": [
    {
      "id": 1,
      "otaChannel": "YANOLJA",
      "guestName": "홍길동",
      "checkInDate": "2026-05-01",
      "checkOutDate": "2026-05-03",
      "status": "CONFIRMED"
    }
  ]
}
```

**GET /api/room-types/status?date=2026-05-01 응답 예시**
```json
[
  {
    "roomTypeId": 1,
    "name": "스탠다드룸",
    "grade": "STANDARD",
    "totalCount": 15,
    "bookedCount": 7,
    "availableCount": 8,
    "otaAllotment": [
      { "channel": "YANOLJA",  "remaining": 3 },
      { "channel": "YEOGI",    "remaining": 5 },
      { "channel": "YOGISEO",  "remaining": 10 }
    ]
  }
]
```

### 통계 / 판매 현황 ★
```
GET  /api/stats/daily?date=2026-05-01
GET  /api/stats/channel/{channel}?date=2026-05-01
```

**일별 통계 응답 예시**
```json
{
  "date": "2026-05-01",
  "totalRooms": 21,
  "bookedRooms": 15,
  "availableRooms": 6,
  "totalRevenue": 3750000,
  "byChannel": [
    { "channel": "YANOLJA",  "bookedRooms": 7, "revenue": 1750000 },
    { "channel": "YEOGI",    "bookedRooms": 5, "revenue": 1250000 },
    { "channel": "YOGISEO",  "bookedRooms": 3, "revenue": 750000  }
  ]
}
```

### 일마감 (ADMIN)
```
POST /api/admin/night-audit
```

---

## 5. 핵심 기술 과제 (면접 답변 포인트)

### 과제 1 - 동시 예약 중복 방지

시나리오: OTA 3개에서 동시에 마지막 1개 실제 객실에 예약 요청

| 단계 | 방법 | 결과 |
|---|---|---|
| 1단계 | 단순 구현 + CountDownLatch 30 스레드 테스트 | available_count 마이너스 확인 (문제 재현) |
| 2단계 | DB 비관적 락 `@Lock(PESSIMISTIC_WRITE)` | 중복 방지 성공, BUT 커넥션 고갈 위험 |
| 3단계 | Redis 분산락 (Redisson) | DB 부하 감소 + 데드락 방지 |

> 면접 답변: "OTA 3개 채널에서 동시에 마지막 객실에 예약이 들어오는 상황을 구현했습니다. DB 비관적 락으로 해결했다가 커넥션 고갈 위험으로 Redis 분산락으로 교체했습니다. 실제 재고와 OTA별 할당 재고 두 곳을 원자적으로 차감하는 부분이 핵심이었습니다."

### 과제 2 - PENDING TTL 자동 해제

> OTA 선점 후 10분 내 확정 없으면 CANCELLED + 재고 복구
> Redis SETEX TTL=600 + Spring Scheduler 활용

### 과제 3 - 멱등성

> OTA 네트워크 오류로 동일 예약 2번 전송 시 처리
> `ota_request_log` UNIQUE KEY (ota_channel, ota_reservation_id) → 중복 시 기존 예약 ID 반환

### 과제 4 - 야간감사 원자성

> NO_SHOW 전환 + 재고 복구 + 날짜 롤링을 @Transactional 로 묶어서 원자적 처리

---

## 6. 개발 순서

- **Phase 1** - 기본 구조 (Spring Boot, MySQL, Swagger 세팅 / Hotel, RoomType CRUD / 재고 초기화)
- **Phase 2** - OTA 예약 수신 핵심 로직 (재고 2중 검사 + 차감 / 취소 / 확정)
- **Phase 3** - 동시성 처리 (문제 재현 테스트 → Pessimistic Lock → Redis 분산락)
- **Phase 4** - 호텔 운영 API (체크인/아웃 / 조기퇴실 / 레이트체크인 / 야간감사)
- **Phase 5** - 통계 & 마무리 (일별 판매 현황 API / Swagger 문서화 / README)

---

## 7. 기술 스택

| 항목 | 내용 |
|---|---|
| Backend | Spring Boot 3.x, Java 17 |
| DB | MySQL 8.x |
| Cache/Lock | Redis, Redisson (Phase 3~) |
| Docs | SpringDoc OpenAPI (Swagger) |
| Build | Gradle |
| OTA Mock | WireMock 또는 별도 Spring Boot 모듈 (Phase 2~) |

---

## 8. README에 반드시 들어갈 내용

1. 프로젝트 배경 (CMS팀 OTA 연동 실무 경험 바탕)
2. 채널 매니저 구조 다이어그램
3. 재고 이중 검사 로직 설명 (OTA 할당 + 실제 재고)
4. 동시성 문제 해결 과정 (3단계)
5. 동시 요청 테스트 결과 (30 스레드 → 실제 재고 21개만 예약 성공)
6. 야간감사 플로우

---

> 이 계획서는 1차 초안입니다. 개발 진행 중 조정 가능.
