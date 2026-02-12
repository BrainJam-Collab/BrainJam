package com.puzzleroom.room.dto;

import com.puzzleroom.room.RoomTask;

import java.time.Instant;
import java.util.UUID;

public class RoomTaskResponse {
    public UUID id;
    public UUID taskId;
    public String title;
    public String prompt;
    public UUID assignedToId;
    public String assignedToUsername;
    public String status;
    public Instant startedAt;
    public Instant completedAt;
    public Long durationSeconds;

    public static RoomTaskResponse from(RoomTask rt, Long durationSeconds) {
        RoomTaskResponse r = new RoomTaskResponse();
        r.id = rt.getId();
        r.taskId = rt.getTask().getId();
        r.title = rt.getTask().getTitle();
        r.prompt = rt.getTask().getPrompt();
        r.assignedToId = rt.getAssignedTo().getId();
        r.assignedToUsername = rt.getAssignedTo().getUsername();
        r.status = rt.getStatus().name();
        r.startedAt = rt.getStartedAt();
        r.completedAt = rt.getCompletedAt();
        r.durationSeconds = durationSeconds;
        return r;
    }
}
