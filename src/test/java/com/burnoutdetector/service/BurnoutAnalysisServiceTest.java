package com.burnoutdetector.service;

import com.burnoutdetector.model.CalendarSummary;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BurnoutAnalysisServiceTest {

    private final BurnoutAnalysisService service = new BurnoutAnalysisService();

    private CalendarSummary summary(int totalMeetings, double totalHours, String earliestMeeting,
                                     int weekendMeetings, int backToBackMeetings, int lateNightMeetings,
                                     int noBreakDays) {
        return new CalendarSummary(totalMeetings, totalHours, earliestMeeting, "-",
                weekendMeetings, backToBackMeetings, lateNightMeetings, noBreakDays);
    }

    @Test
    void baselineSummaryScoresZero() {
        CalendarSummary baseline = summary(0, 0, "-", 0, 0, 0, 0);

        assertThat(service.calculateScore(baseline)).isZero();
    }

    @Test
    void meetingCountAtOrBelowTenAddsNothing() {
        assertThat(service.calculateScore(summary(10, 0, "-", 0, 0, 0, 0))).isZero();
    }

    @Test
    void meetingCountAboveTenAddsFifteen() {
        assertThat(service.calculateScore(summary(11, 0, "-", 0, 0, 0, 0))).isEqualTo(15);
    }

    @Test
    void meetingCountAboveTwentyAddsThirty() {
        assertThat(service.calculateScore(summary(21, 0, "-", 0, 0, 0, 0))).isEqualTo(30);
    }

    @Test
    void hoursAtOrBelowTenAddsNothing() {
        assertThat(service.calculateScore(summary(0, 10, "-", 0, 0, 0, 0))).isZero();
    }

    @Test
    void hoursAboveTenAddsFifteen() {
        assertThat(service.calculateScore(summary(0, 10.5, "-", 0, 0, 0, 0))).isEqualTo(15);
    }

    @Test
    void hoursAboveTwentyAddsThirty() {
        assertThat(service.calculateScore(summary(0, 20.5, "-", 0, 0, 0, 0))).isEqualTo(30);
    }

    @Test
    void anyWeekendMeetingAddsTwenty() {
        assertThat(service.calculateScore(summary(0, 0, "-", 1, 0, 0, 0))).isEqualTo(20);
    }

    @Test
    void backToBackAtOrBelowTwoAddsNothing() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 2, 0, 0))).isZero();
    }

    @Test
    void backToBackAboveTwoAddsTen() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 3, 0, 0))).isEqualTo(10);
    }

    @Test
    void backToBackAboveFiveAddsTwenty() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 6, 0, 0))).isEqualTo(20);
    }

    @Test
    void lateNightAtZeroAddsNothing() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 0, 0, 0))).isZero();
    }

    @Test
    void lateNightAboveZeroAddsTen() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 0, 1, 0))).isEqualTo(10);
    }

    @Test
    void lateNightAboveThreeAddsTwenty() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 0, 4, 0))).isEqualTo(20);
    }

    @Test
    void noBreakDaysAtOrBelowOneAddsNothing() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 0, 0, 1))).isZero();
    }

    @Test
    void noBreakDaysAboveOneAddsTen() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 0, 0, 2))).isEqualTo(10);
    }

    @Test
    void noBreakDaysAboveThreeAddsTwenty() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 0, 0, 4))).isEqualTo(20);
    }

    @Test
    void earliestMeetingBeforeEightAmAddsTwenty() {
        assertThat(service.calculateScore(summary(0, 0, "7:30 AM", 0, 0, 0, 0))).isEqualTo(20);
    }

    @Test
    void earliestMeetingAtOrAfterEightAmAddsNothing() {
        assertThat(service.calculateScore(summary(0, 0, "8:00 AM", 0, 0, 0, 0))).isZero();
    }

    @Test
    void noEarliestMeetingIsIgnored() {
        assertThat(service.calculateScore(summary(0, 0, "-", 0, 0, 0, 0))).isZero();
    }

    @Test
    void scoreIsCappedAtOneHundredEvenWhenEveryFactorMaxesOut() {
        CalendarSummary worstCase = summary(25, 25, "6:00 AM", 3, 6, 4, 4);

        assertThat(service.calculateScore(worstCase)).isEqualTo(100);
    }
}
