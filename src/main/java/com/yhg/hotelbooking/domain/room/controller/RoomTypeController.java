package com.yhg.hotelbooking.domain.room.controller;

import com.yhg.hotelbooking.domain.room.dto.request.RoomTypeRequest;
import com.yhg.hotelbooking.domain.room.dto.response.RoomTypeResponse;
import com.yhg.hotelbooking.domain.room.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PostMapping
    public ResponseEntity<RoomTypeResponse> createRoomType(@Valid @RequestBody RoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.createRoomType(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeResponse>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes());
    }


}
