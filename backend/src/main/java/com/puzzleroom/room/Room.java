package com.puzzleroom.room;

import com.puzzleroom.puzzle.Puzzle;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {

    public static final int MAX_MEMBERS = 4;

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private boolean ownerParticipates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.OPEN;

    @ManyToOne(optional = false)
    @JoinColumn(name = "puzzle_id", nullable = false)
    private Puzzle puzzle;

    @Column(nullable = false, unique = true, length = 20)
    private String inviteCode;

    @Column(nullable = false)
    private int maxMembers = MAX_MEMBERS;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant startedAt;
    private Instant completedAt;

    protected Room() {}

    public Room(UUID ownerId, boolean ownerParticipates, Puzzle puzzle, String inviteCode) {
        this.ownerId = ownerId;
        this.ownerParticipates = ownerParticipates;
        this.puzzle = puzzle;
        this.inviteCode = inviteCode;
        this.status = RoomStatus.OPEN;
        this.maxMembers = MAX_MEMBERS;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public boolean isOwnerParticipates() { return ownerParticipates; }
    public RoomStatus getStatus() { return status; }
    public Puzzle getPuzzle() { return puzzle; }
    public String getInviteCode() { return inviteCode; }
    public int getMaxMembers() { return maxMembers; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }

    public void setStatus(RoomStatus status) { this.status = status; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
