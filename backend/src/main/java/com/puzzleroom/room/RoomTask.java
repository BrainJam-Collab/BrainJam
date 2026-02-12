package com.puzzleroom.room;

import com.puzzleroom.puzzle.PuzzleTask;
import com.puzzleroom.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "room_tasks", uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_task_assigned", columnNames = {"room_id", "assigned_to_id"})
})
public class RoomTask {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private PuzzleTask task;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assigned_to_id", nullable = false)
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomTaskStatus status = RoomTaskStatus.ASSIGNED;

    private Instant startedAt;
    private Instant completedAt;

    protected RoomTask() {}

    public RoomTask(Room room, PuzzleTask task, User assignedTo, Instant startedAt) {
        this.room = room;
        this.task = task;
        this.assignedTo = assignedTo;
        this.startedAt = startedAt;
        this.status = RoomTaskStatus.ASSIGNED;
    }

    public UUID getId() { return id; }
    public Room getRoom() { return room; }
    public PuzzleTask getTask() { return task; }
    public User getAssignedTo() { return assignedTo; }
    public RoomTaskStatus getStatus() { return status; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }

    public void markCompleted(Instant completedAt) {
        this.completedAt = completedAt;
        this.status = RoomTaskStatus.COMPLETED;
    }
}
