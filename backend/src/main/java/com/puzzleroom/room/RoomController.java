package com.puzzleroom.room;

import com.puzzleroom.room.dto.CreateRoomRequest;
import com.puzzleroom.room.dto.JoinRoomRequest;
import com.puzzleroom.room.dto.RoomResponse;
import com.puzzleroom.room.dto.RoomTaskResponse;
import com.puzzleroom.room.dto.UpdateReadyRequest;
import com.puzzleroom.room.dto.UpdateRoomSettingsRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService rooms;

    public RoomController(RoomService rooms) {
        this.rooms = rooms;
    }

    @PostMapping
    public RoomResponse create(@Valid @RequestBody CreateRoomRequest req, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.createRoom(userId, req.ownerParticipates, req.puzzleId);
    }

    @PostMapping("/join")
    public RoomResponse join(@Valid @RequestBody JoinRoomRequest req, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.joinByInvite(req.inviteCode.trim().toUpperCase(), userId);
    }

    @GetMapping("/{roomId}")
    public RoomResponse get(@PathVariable UUID roomId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.getRoom(roomId, userId);
    }

    @PostMapping("/{roomId}/start")
    public List<RoomTaskResponse> start(@PathVariable UUID roomId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.startRoom(roomId, userId);
    }

    @PatchMapping("/{roomId}/settings")
    public RoomResponse settings(
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomSettingsRequest req,
            Authentication auth
    ) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.updateSettings(roomId, userId, req.locked, req.maxMembers);
    }

    @PostMapping("/{roomId}/invite-code/regenerate")
    public RoomResponse regenerateInviteCode(@PathVariable UUID roomId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.regenerateInviteCode(roomId, userId);
    }

    @DeleteMapping("/{roomId}/members/{memberUserId}")
    public RoomResponse removeMember(
            @PathVariable UUID roomId,
            @PathVariable UUID memberUserId,
            Authentication auth
    ) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.removeMember(roomId, memberUserId, userId);
    }

    @PatchMapping("/{roomId}/ready")
    public RoomResponse ready(
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateReadyRequest req,
            Authentication auth
    ) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.updateReady(roomId, userId, req.ready);
    }

    @GetMapping("/{roomId}/tasks")
    public List<RoomTaskResponse> tasks(@PathVariable UUID roomId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rooms.listTasks(roomId, userId);
    }
}
