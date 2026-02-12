package com.puzzleroom.puzzle;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PuzzleSeeder implements CommandLineRunner {

    private final PuzzleRepository puzzles;

    public PuzzleSeeder(PuzzleRepository puzzles) {
        this.puzzles = puzzles;
    }

    @Override
    public void run(String... args) {
        if (puzzles.count() > 0) return;

        Puzzle logic = new Puzzle(
                "Signal Logic",
                PuzzleGenre.LOGIC,
                "Decode constraints to open the final gate."
        );
        logic.addTask(new PuzzleTask("Logic Grid", "Solve the 4x4 logic grid from the given clues.", 1));
        logic.addTask(new PuzzleTask("Switch Order", "Determine the correct order of switches to light all panels.", 2));
        logic.addTask(new PuzzleTask("Truth Table", "Find the missing value that satisfies all boolean rules.", 3));
        logic.addTask(new PuzzleTask("Final Lock", "Combine the three previous answers to form the final key.", 4));

        Puzzle word = new Puzzle(
                "Cipher Relay",
                PuzzleGenre.WORD,
                "Crack layered word puzzles to reach the message."
        );
        word.addTask(new PuzzleTask("Anagram Stack", "Unscramble the words to reveal a hidden phrase.", 1));
        word.addTask(new PuzzleTask("Caesar Shift", "Decode the sentence shifted by an unknown offset.", 2));
        word.addTask(new PuzzleTask("Word Ladder", "Transform the start word into the end word in 5 steps.", 3));
        word.addTask(new PuzzleTask("Meta Answer", "Use the decoded words to find the final answer.", 4));

        puzzles.save(logic);
        puzzles.save(word);
    }
}
