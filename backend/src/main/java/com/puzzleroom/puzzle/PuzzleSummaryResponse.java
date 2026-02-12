package com.puzzleroom.puzzle;

import java.util.UUID;

public class PuzzleSummaryResponse {
    public UUID id;
    public String title;
    public String genre;
    public String description;
    public int taskCount;

    static PuzzleSummaryResponse from(Puzzle p) {
        PuzzleSummaryResponse r = new PuzzleSummaryResponse();
        r.id = p.getId();
        r.title = p.getTitle();
        r.genre = p.getGenre().name();
        r.description = p.getDescription();
        r.taskCount = p.getTasks().size();
        return r;
    }
}
