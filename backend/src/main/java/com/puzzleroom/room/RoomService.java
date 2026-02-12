package com.puzzleroom.room;

import com.puzzleroom.common.InviteCodeUtil;
import com.puzzleroom.puzzle.Puzzle;
import com.puzzleroom.puzzle.PuzzleRepository;
import com.puzzleroom.puzzle.PuzzleTask;
import com.puzzleroom.puzzle.PuzzleTaskRepository;
import com.puzzleroom.room.dto.MemberResponse;
import com.puzzleroom.room.dto.RoomResponse;
import com.puzzleroom.room.dto.RoomTaskResponse;
import com.puzzleroom.realtime.RoomEventPublisher;
import com.puzzleroom.user.User;
import com.puzzleroom.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository rooms;
    private final RoomMemberRepository members;
    private final RoomTaskRepository roomTasks;
    private final PuzzleRepository puzzles;
    private final PuzzleTaskRepository puzzleTasks;
    private final UserRepository users;
    private final RoomEventPublisher events;

    public RoomService(
            RoomRepository rooms,
            RoomMemberRepository members,
            RoomTaskRepository roomTasks,
            PuzzleRepository puzzles,
            PuzzleTaskRepository puzzleTasks,
            UserRepository users,
            RoomEventPublisher events
    ) {
        this.rooms = rooms;
        this.members = members;
        this.roomTasks = roomTasks;
        this.puzzles = puzzles;
        this.puzzleTasks = puzzleTasks;
        this.users = users;
        this.events = events;
    }

    public RoomResponse createRoom(UUID ownerId, boolean ownerParticipates, UUID puzzleId) {
        Puzzle puzzle = puzzles.findById(puzzleId)
                .orElseThrow(() -> new IllegalArgumentException("puzzle not found"));

        String invite = generateInviteCode();
        Room room = new Room(ownerId, ownerParticipates, puzzle, invite);
        rooms.save(room);

        if (ownerParticipates) {
            User owner = requireUser(ownerId);
            members.save(new RoomMember(room, owner, RoomRole.OWNER));
        }

        events.roomUpdated(room.getId());
        return buildRoomResponse(room);
    }

    public RoomResponse joinByInvite(String inviteCode, UUID userId) {
        Room room = rooms.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("invalid invite code"));

        if (room.getStatus() == RoomStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("room already started");
        }
        if (room.getStatus() == RoomStatus.COMPLETED) {
            throw new IllegalArgumentException("room already completed");
        }

        if (!room.isOwnerParticipates() && room.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("owner chose not to participate");
        }

        if (members.findByRoomIdAndUserId(room.getId(), userId).isPresent()) {
            return buildRoomResponse(room);
        }

        long count = members.countByRoomId(room.getId());
        if (count >= room.getMaxMembers()) {
            throw new IllegalArgumentException("room is full");
        }

        User user = requireUser(userId);
        members.save(new RoomMember(room, user, RoomRole.MEMBER));

        events.roomUpdated(room.getId());
        return buildRoomResponse(room);
    }

    public RoomResponse getRoom(UUID roomId, UUID userId) {
        Room room = requireRoom(roomId);
        assertMemberOrOwner(room, userId);
        return buildRoomResponse(room);
    }

    public List<RoomTaskResponse> startRoom(UUID roomId, UUID userId) {
        Room room = requireRoom(roomId);
        if (!room.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("only the owner can start the room");
        }
        if (room.getStatus() != RoomStatus.OPEN) {
            throw new IllegalArgumentException("room already started");
        }

        List<RoomMember> roomMembers = members.findByRoomId(roomId);
        if (roomMembers.isEmpty()) {
            throw new IllegalArgumentException("at least one participant required");
        }

        List<PuzzleTask> tasks = puzzleTasks.findByPuzzleIdOrderByOrderIndexAsc(room.getPuzzle().getId());
        if (tasks.size() < roomMembers.size()) {
            throw new IllegalArgumentException("not enough tasks for participants");
        }

        List<PuzzleTask> shuffled = new ArrayList<>(tasks);
        Collections.shuffle(shuffled);

        Instant startedAt = Instant.now();
        room.setStatus(RoomStatus.IN_PROGRESS);
        room.setStartedAt(startedAt);
        rooms.save(room);

        List<RoomTask> created = new ArrayList<>();
        for (int i = 0; i < roomMembers.size(); i++) {
            PuzzleTask task = shuffled.get(i);
            RoomMember member = roomMembers.get(i);
            created.add(new RoomTask(room, task, member.getUser(), startedAt));
        }
        roomTasks.saveAll(created);

        events.roomUpdated(room.getId());
        return created.stream()
                .map(rt -> RoomTaskResponse.from(rt, durationSeconds(room, rt)))
                .collect(Collectors.toList());
    }

    public List<RoomTaskResponse> listTasks(UUID roomId, UUID userId) {
        Room room = requireRoom(roomId);
        assertMemberOrOwner(room, userId);
        return roomTasks.findByRoomId(roomId).stream()
                .map(rt -> RoomTaskResponse.from(rt, durationSeconds(room, rt)))
                .collect(Collectors.toList());
    }

    public RoomTaskResponse completeTask(UUID roomTaskId, UUID userId) {
        RoomTask rt = roomTasks.findById(roomTaskId)
                .orElseThrow(() -> new IllegalArgumentException("task not found"));

        Room room = rt.getRoom();
        if (!rt.getAssignedTo().getId().equals(userId)) {
            throw new IllegalArgumentException("task not assigned to user");
        }
        if (rt.getStatus() == RoomTaskStatus.COMPLETED) {
            return RoomTaskResponse.from(rt, durationSeconds(room, rt));
        }

        Instant completedAt = Instant.now();
        rt.markCompleted(completedAt);
        roomTasks.save(rt);

        long remaining = roomTasks.countByRoomIdAndStatus(room.getId(), RoomTaskStatus.ASSIGNED);
        if (remaining == 0 && room.getStatus() != RoomStatus.COMPLETED) {
            room.setStatus(RoomStatus.COMPLETED);
            room.setCompletedAt(completedAt);
            rooms.save(room);
        }

        events.roomUpdated(room.getId());
        return RoomTaskResponse.from(rt, durationSeconds(room, rt));
    }

    private Room requireRoom(UUID roomId) {
        return rooms.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));
    }

    private User requireUser(UUID userId) {
        return users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    private void assertMemberOrOwner(Room room, UUID userId) {
        if (room.getOwnerId().equals(userId)) return;
        if (members.findByRoomIdAndUserId(room.getId(), userId).isPresent()) return;
        throw new IllegalArgumentException("not a room member");
    }

    private String generateInviteCode() {
        String code = InviteCodeUtil.generate(8);
        while (rooms.existsByInviteCode(code)) {
            code = InviteCodeUtil.generate(8);
        }
        return code;
    }

    private Long durationSeconds(Room room, RoomTask rt) {
        if (room.getStartedAt() == null || rt.getCompletedAt() == null) return null;
        return Duration.between(room.getStartedAt(), rt.getCompletedAt()).getSeconds();
    }

    private RoomResponse buildRoomResponse(Room room) {
        List<MemberResponse> memberResponses = members.findByRoomId(room.getId()).stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());

        Long teamTime = computeTeamTimeSeconds(room);
        return RoomResponse.from(room, memberResponses, teamTime);
    }

    private Long computeTeamTimeSeconds(Room room) {
        if (room.getStatus() != RoomStatus.COMPLETED || room.getStartedAt() == null || room.getCompletedAt() == null) {
            return null;
        }
        List<RoomTask> tasks = roomTasks.findByRoomId(room.getId());
        long max = 0;
        for (RoomTask rt : tasks) {
            Long duration = durationSeconds(room, rt);
            if (duration != null && duration > max) max = duration;
        }
        return max;
    }
}
