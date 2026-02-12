package com.puzzleroom.room.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinRoomRequest {
    @NotBlank
    public String inviteCode;
}
