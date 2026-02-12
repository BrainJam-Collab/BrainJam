package com.puzzleroom.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = {"email"}),
        @UniqueConstraint(name = "uk_users_username", columnNames = {"username"})
})
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected User() {}

    public User(String email, String username, String passwordHash) {
        this.email = email.toLowerCase();
        this.username = username.toLowerCase();
        this.passwordHash = passwordHash;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }

    public void setEmail(String email) { this.email = email.toLowerCase(); }
    public void setUsername(String username) { this.username = username.toLowerCase(); }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
