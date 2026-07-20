package com.burnoutdetector.service;

import com.burnoutdetector.model.CalendarSummary;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalendarServiceTest {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final CalendarService calendarService = new CalendarService();

    private Event timedEvent(LocalDate date, LocalTime start, LocalTime end) {
        long startMs = ZonedDateTime.of(date, start, ZONE).toInstant().toEpochMilli();
        long endMs = ZonedDateTime.of(date, end, ZONE).toInstant().toEpochMilli();

        return new Event()
                .setStart(new EventDateTime().setDateTime(new DateTime(startMs)))
                .setEnd(new EventDateTime().setDateTime(new DateTime(endMs)));
    }

    @Test
    void emptyEventListProducesZeroedSummary() {
        CalendarSummary summary = calendarService.buildSummary(new ArrayList<>());

        assertThat(summary.getTotalMeetings()).isZero();
        assertThat(summary.getTotalHours()).isZero();
        assertThat(summary.getEarliestMeeting()).isEqualTo("-");
        assertThat(summary.getLatestMeeting()).isEqualTo("-");
        assertThat(summary.getWeekendMeetings()).isZero();
        assertThat(summary.getBackToBackMeetings()).isZero();
        assertThat(summary.getLateNightMeetings()).isZero();
        assertThat(summary.getNoBreakDays()).isZero();
    }

    @Test
    void countsTotalMeetingsAndHours() {
        LocalDate monday = LocalDate.of(2026, 7, 20);
        List<Event> events = List.of(
                timedEvent(monday, LocalTime.of(9, 0), LocalTime.of(10, 0)),
                timedEvent(monday, LocalTime.of(11, 0), LocalTime.of(12, 30))
        );

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getTotalMeetings()).isEqualTo(2);
        assertThat(summary.getTotalHours()).isEqualTo(2.5);
    }

    @Test
    void countsAllDayEventsAsMeetingsButExcludesThemFromHours() {
        Event allDayEvent = new Event().setStart(new EventDateTime()).setEnd(new EventDateTime());
        LocalDate monday = LocalDate.of(2026, 7, 20);
        List<Event> events = new ArrayList<>();
        events.add(allDayEvent);
        events.add(timedEvent(monday, LocalTime.of(9, 0), LocalTime.of(10, 0)));

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getTotalMeetings()).isEqualTo(2);
        assertThat(summary.getTotalHours()).isEqualTo(1.0);
    }

    @Test
    void detectsWeekendMeetings() {
        LocalDate saturday = LocalDate.of(2026, 7, 18);
        List<Event> events = List.of(timedEvent(saturday, LocalTime.of(9, 0), LocalTime.of(10, 0)));

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getWeekendMeetings()).isEqualTo(1);
    }

    @Test
    void weekdayMeetingIsNotCountedAsWeekend() {
        LocalDate monday = LocalDate.of(2026, 7, 20);
        List<Event> events = List.of(timedEvent(monday, LocalTime.of(9, 0), LocalTime.of(10, 0)));

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getWeekendMeetings()).isZero();
    }

    @Test
    void detectsBackToBackMeetingsWithinFifteenMinuteGap() {
        LocalDate monday = LocalDate.of(2026, 7, 20);
        List<Event> events = List.of(
                timedEvent(monday, LocalTime.of(9, 0), LocalTime.of(10, 0)),
                timedEvent(monday, LocalTime.of(10, 5), LocalTime.of(11, 0)),
                timedEvent(monday, LocalTime.of(13, 0), LocalTime.of(14, 0))
        );

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getBackToBackMeetings()).isEqualTo(1);
    }

    @Test
    void detectsLateNightMeetingsEndingAtOrAfterSevenPm() {
        LocalDate monday = LocalDate.of(2026, 7, 20);
        List<Event> events = List.of(
                timedEvent(monday, LocalTime.of(18, 0), LocalTime.of(19, 0)),
                timedEvent(monday, LocalTime.of(15, 0), LocalTime.of(16, 0))
        );

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getLateNightMeetings()).isEqualTo(1);
    }

    @Test
    void detectsDaysWithNoThirtyMinuteBreak() {
        LocalDate monday = LocalDate.of(2026, 7, 20);
        LocalDate tuesday = LocalDate.of(2026, 7, 21);
        List<Event> events = List.of(
                timedEvent(monday, LocalTime.of(9, 0), LocalTime.of(10, 0)),
                timedEvent(monday, LocalTime.of(10, 15), LocalTime.of(11, 0)),
                timedEvent(tuesday, LocalTime.of(9, 0), LocalTime.of(10, 0)),
                timedEvent(tuesday, LocalTime.of(10, 45), LocalTime.of(11, 30))
        );

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getNoBreakDays()).isEqualTo(1);
    }

    @Test
    void tracksEarliestAndLatestMeetingTimes() {
        LocalDate monday = LocalDate.of(2026, 7, 20);
        List<Event> events = List.of(
                timedEvent(monday, LocalTime.of(14, 0), LocalTime.of(15, 0)),
                timedEvent(monday, LocalTime.of(7, 30), LocalTime.of(8, 0)),
                timedEvent(monday, LocalTime.of(16, 0), LocalTime.of(17, 0))
        );

        CalendarSummary summary = calendarService.buildSummary(events);

        assertThat(summary.getEarliestMeeting()).isEqualTo("7:30 AM");
        assertThat(summary.getLatestMeeting()).isEqualTo("4:00 PM");
    }
}
