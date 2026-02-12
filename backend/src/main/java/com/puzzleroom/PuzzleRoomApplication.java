package com.puzzleroom;

import com.puzzleroom.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class PuzzleRoomApplication {
    public static void main(String[] args) {
        SpringApplication.run(PuzzleRoomApplication.class, args);
    }
}
