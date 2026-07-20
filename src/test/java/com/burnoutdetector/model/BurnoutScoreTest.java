package com.burnoutdetector.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BurnoutScoreTest {

    @Test
    void exposesAllConstructorValuesThroughGetters() {
        LocalDateTime recordedAt = LocalDateTime.of(2026, 7, 19, 14, 30);
        BurnoutScore score = new BurnoutScore("user@example.com", 75, "High Risk", recordedAt);

        assertThat(score.getUserEmail()).isEqualTo("user@example.com");
        assertThat(score.getScore()).isEqualTo(75);
        assertThat(score.getRiskLevel()).isEqualTo("High Risk");
        assertThat(score.getRecordedAt()).isEqualTo(recordedAt);
    }

    @Test
    void formatsRecordedAtAsMonthDayYearTime() {
        BurnoutScore score = new BurnoutScore("user@example.com", 50, "Moderate Risk",
                LocalDateTime.of(2026, 7, 19, 14, 30));

        assertThat(score.getFormattedDate()).isEqualTo("Jul 19, 2026 2:30 PM");
    }

    @Test
    void noArgsConstructorLeavesFieldsUnset() {
        BurnoutScore score = new BurnoutScore();

        assertThat(score.getId()).isNull();
        assertThat(score.getUserEmail()).isNull();
        assertThat(score.getRiskLevel()).isNull();
        assertThat(score.getRecordedAt()).isNull();
    }
}
