package com.puzzleroom.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 24)
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_.-]{3,24}$")
    public String username;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    @Size(min = 8, max = 72)
    public String password;
}
