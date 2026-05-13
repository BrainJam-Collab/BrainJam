package com.puzzleroom.room.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateRoomSettingsRequest {
    @NotNull
    public Boolean locked;

    @NotNull
    @Min(2)
    @Max(4)
    public Integer maxMembers;
}
