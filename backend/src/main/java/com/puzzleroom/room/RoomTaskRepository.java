package com.puzzleroom.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RoomTaskRepository extends JpaRepository<RoomTask, UUID> {
    List<RoomTask> findByRoomId(UUID roomId);
    long countByRoomIdAndStatus(UUID roomId, RoomTaskStatus status);
}
