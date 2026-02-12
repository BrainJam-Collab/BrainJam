package com.puzzleroom.puzzle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/puzzles")
public class PuzzleController {

    private final PuzzleRepository puzzles;

    public PuzzleController(PuzzleRepository puzzles) {
        this.puzzles = puzzles;
    }

    @GetMapping
    public List<PuzzleSummaryResponse> list(@RequestParam(name = "genre", required = false) PuzzleGenre genre) {
        return puzzles.findAll().stream()
                .filter(p -> genre == null || p.getGenre() == genre)
                .map(PuzzleSummaryResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/genres")
    public List<String> genres() {
        return List.of(
                PuzzleGenre.LOGIC.name(),
                PuzzleGenre.WORD.name(),
                PuzzleGenre.MATH.name(),
                PuzzleGenre.PATTERN.name(),
                PuzzleGenre.RIDDLE.name()
        );
    }
}
