package com.burnoutdetector.demo.service;

import com.burnoutdetector.demo.model.CalendarSummary;
import org.springframework.stereotype.Service;

@Service
public class BurnoutAnalysisService {

    public int calculateScore(CalendarSummary summary) {
        int score = 0;

        if (summary.getTotalMeetings() > 20) score += 30;
        else if (summary.getTotalMeetings() > 10) score += 15;

        if (summary.getTotalHours() > 20) score += 30;
        else if (summary.getTotalHours() > 10) score += 15;

        if (summary.getWeekendMeetings() > 0) score += 20;

        if (summary.getBackToBackMeetings() > 5) score += 20;
        else if (summary.getBackToBackMeetings() > 2) score += 10;

        if (summary.getLateNightMeetings() > 3) score += 20;
        else if (summary.getLateNightMeetings() > 0) score += 10;

        if (summary.getNoBreakDays() > 3) score += 20;
        else if (summary.getNoBreakDays() > 1) score += 10;

        if (!summary.getEarliestMeeting().equals("-")) {
            int hour = Integer.parseInt(summary.getEarliestMeeting().split(":")[0]);
            if (hour < 8) score += 20;
        }

        return Math.min(score, 100);
    }

}