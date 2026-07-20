package com.burnoutdetector.repository;

import com.burnoutdetector.model.BurnoutScore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BurnoutScoreRepositoryTest {

    @Autowired
    private BurnoutScoreRepository repository;

    @Test
    void findsScoresForUserOrderedByMostRecentFirst() {
        LocalDateTime now = LocalDateTime.now();
        repository.save(new BurnoutScore("user@example.com", 40, "Moderate Risk", now.minusDays(2)));
        repository.save(new BurnoutScore("user@example.com", 80, "High Risk", now));
        repository.save(new BurnoutScore("user@example.com", 60, "Moderate Risk", now.minusDays(1)));
        repository.save(new BurnoutScore("other@example.com", 10, "Low Risk", now));

        List<BurnoutScore> scores = repository.findByUserEmailOrderByRecordedAtDesc("user@example.com");

        assertThat(scores).extracting(BurnoutScore::getScore).containsExactly(80, 60, 40);
        assertThat(scores).allMatch(score -> score.getUserEmail().equals("user@example.com"));
    }

    @Test
    void returnsEmptyListWhenUserHasNoScores() {
        List<BurnoutScore> scores = repository.findByUserEmailOrderByRecordedAtDesc("nobody@example.com");

        assertThat(scores).isEmpty();
    }
}
