package com.puzzleroom.room.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateReadyRequest {
    @NotNull
    public Boolean ready;
}
