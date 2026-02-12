package com.puzzleroom.realtime;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RoomEventPublisher {

    private final RoomSocketHandler socketHandler;

    public RoomEventPublisher(RoomSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    public void roomUpdated(UUID roomId) {
        socketHandler.broadcast(roomId, "refresh");
    }
}
