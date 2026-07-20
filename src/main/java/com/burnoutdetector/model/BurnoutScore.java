package com.burnoutdetector.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class BurnoutScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private int score;
    private String riskLevel;
    private LocalDateTime recordedAt;

    public BurnoutScore() {}

    public BurnoutScore(String userEmail, int score, String riskLevel, LocalDateTime recordedAt) {
        this.userEmail = userEmail;
        this.score = score;
        this.riskLevel = riskLevel;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public int getScore() { return score; }
    public String getRiskLevel() { return riskLevel; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    public String getFormattedDate() {
        return recordedAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
    }
}
