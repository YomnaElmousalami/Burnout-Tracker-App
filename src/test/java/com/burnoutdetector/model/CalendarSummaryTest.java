package com.burnoutdetector.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalendarSummaryTest {

    @Test
    void exposesAllConstructorValuesThroughGetters() {
        CalendarSummary summary = new CalendarSummary(12, 15.5, "9:00 AM", "5:00 PM", 2, 3, 1, 0);

        assertThat(summary.getTotalMeetings()).isEqualTo(12);
        assertThat(summary.getTotalHours()).isEqualTo(15.5);
        assertThat(summary.getEarliestMeeting()).isEqualTo("9:00 AM");
        assertThat(summary.getLatestMeeting()).isEqualTo("5:00 PM");
        assertThat(summary.getWeekendMeetings()).isEqualTo(2);
        assertThat(summary.getBackToBackMeetings()).isEqualTo(3);
        assertThat(summary.getLateNightMeetings()).isEqualTo(1);
        assertThat(summary.getNoBreakDays()).isEqualTo(0);
    }
}
