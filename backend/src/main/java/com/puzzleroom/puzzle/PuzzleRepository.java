package com.puzzleroom.puzzle;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface PuzzleRepository extends JpaRepository<Puzzle, UUID> {
    @Override
    @EntityGraph(attributePaths = "tasks")
    List<Puzzle> findAll();
}
