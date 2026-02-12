package com.puzzleroom.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
    long countByRoomId(UUID roomId);
    List<RoomMember> findByRoomId(UUID roomId);
    Optional<RoomMember> findByRoomIdAndUserId(UUID roomId, UUID userId);
}
