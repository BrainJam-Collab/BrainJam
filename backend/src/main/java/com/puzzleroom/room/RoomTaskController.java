package com.puzzleroom.room;

import com.puzzleroom.room.dto.RoomTaskResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/room-tasks")
public class RoomTaskController {

    private final RoomService rooms;

    public RoomTaskController(RoomService rooms) {
        this.rooms = rooms;
    }

    @PostMapping("/{roomTaskId}/complete")
    public RoomTaskResponse complete(@PathVariable UUID roomTaskId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.completeTask(roomTaskId, userId);
    }
}
