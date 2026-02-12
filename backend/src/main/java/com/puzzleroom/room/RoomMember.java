package com.puzzleroom.room;

import com.puzzleroom.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "room_members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_member", columnNames = {"room_id", "user_id"})
})
public class RoomMember {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomRole role;

    @Column(nullable = false, updatable = false)
    private Instant joinedAt = Instant.now();

    protected RoomMember() {}

    public RoomMember(Room room, User user, RoomRole role) {
        this.room = room;
        this.user = user;
        this.role = role;
    }

    public UUID getId() { return id; }
    public Room getRoom() { return room; }
    public User getUser() { return user; }
    public RoomRole getRole() { return role; }
    public Instant getJoinedAt() { return joinedAt; }
}
