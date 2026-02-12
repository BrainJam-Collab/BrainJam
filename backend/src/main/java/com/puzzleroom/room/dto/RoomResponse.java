package com.puzzleroom.room.dto;

import com.puzzleroom.room.Room;
import com.puzzleroom.room.RoomStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RoomResponse {
    public UUID id;
    public UUID ownerId;
    public boolean ownerParticipates;
    public RoomStatus status;
    public String inviteCode;
    public int maxMembers;
    public Instant createdAt;
    public Instant startedAt;
    public Instant completedAt;
    public Long teamTimeSeconds;

    public PuzzleInfo puzzle;
    public List<MemberResponse> members;

    public static RoomResponse from(Room room, List<MemberResponse> members, Long teamTimeSeconds) {
        RoomResponse r = new RoomResponse();
        r.id = room.getId();
        r.ownerId = room.getOwnerId();
        r.ownerParticipates = room.isOwnerParticipates();
        r.status = room.getStatus();
        r.inviteCode = room.getInviteCode();
        r.maxMembers = room.getMaxMembers();
        r.createdAt = room.getCreatedAt();
        r.startedAt = room.getStartedAt();
        r.completedAt = room.getCompletedAt();
        r.teamTimeSeconds = teamTimeSeconds;
        r.puzzle = PuzzleInfo.from(room);
        r.members = members;
        return r;
    }

    public static class PuzzleInfo {
        public UUID id;
        public String title;
        public String genre;

        static PuzzleInfo from(Room room) {
            PuzzleInfo p = new PuzzleInfo();
            p.id = room.getPuzzle().getId();
            p.title = room.getPuzzle().getTitle();
            p.genre = room.getPuzzle().getGenre().name();
            return p;
        }
    }
}
