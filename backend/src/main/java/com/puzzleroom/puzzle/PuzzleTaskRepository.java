package com.puzzleroom.puzzle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PuzzleTaskRepository extends JpaRepository<PuzzleTask, UUID> {
    List<PuzzleTask> findByPuzzleIdOrderByOrderIndexAsc(UUID puzzleId);
}
