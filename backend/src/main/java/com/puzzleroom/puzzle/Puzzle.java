package com.puzzleroom.puzzle;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "puzzles")
public class Puzzle {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PuzzleGenre genre;

    @Column(nullable = false, length = 1000)
    private String description;

    @OneToMany(mappedBy = "puzzle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuzzleTask> tasks = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Puzzle() {}

    public Puzzle(String title, PuzzleGenre genre, String description) {
        this.title = title;
        this.genre = genre;
        this.description = description;
    }

    public void addTask(PuzzleTask task) {
        task.setPuzzle(this);
        this.tasks.add(task);
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public PuzzleGenre getGenre() { return genre; }
    public String getDescription() { return description; }
    public List<PuzzleTask> getTasks() { return tasks; }
    public Instant getCreatedAt() { return createdAt; }
}
