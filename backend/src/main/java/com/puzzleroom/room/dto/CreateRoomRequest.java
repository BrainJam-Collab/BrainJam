package com.puzzleroom.room.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateRoomRequest {
    @NotNull
    public UUID puzzleId;

    public boolean ownerParticipates;
}
