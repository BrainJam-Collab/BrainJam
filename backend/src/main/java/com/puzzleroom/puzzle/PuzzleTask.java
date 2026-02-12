package com.puzzleroom.puzzle;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "puzzle_tasks")
public class PuzzleTask {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "puzzle_id", nullable = false)
    private Puzzle puzzle;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String prompt;

    @Column(nullable = false)
    private int orderIndex;

    protected PuzzleTask() {}

    public PuzzleTask(String title, String prompt, int orderIndex) {
        this.title = title;
        this.prompt = prompt;
        this.orderIndex = orderIndex;
    }

    public UUID getId() { return id; }
    public Puzzle getPuzzle() { return puzzle; }
    public String getTitle() { return title; }
    public String getPrompt() { return prompt; }
    public int getOrderIndex() { return orderIndex; }

    void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }
}
