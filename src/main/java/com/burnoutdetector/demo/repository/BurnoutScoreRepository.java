package com.burnoutdetector.demo.repository;

import com.burnoutdetector.demo.model.BurnoutScore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BurnoutScoreRepository extends JpaRepository<BurnoutScore, Long> {
    List<BurnoutScore> findByUserEmailOrderByRecordedAtDesc(String userEmail);
}
