package com.puzzleroom.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @JsonAlias({"email", "username"})
    public String identifier;

    public String email;
    public String username;

    @NotBlank
    public String password;
}
