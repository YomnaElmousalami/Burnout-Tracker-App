package com.burnoutdetector.service;

import com.burnoutdetector.model.CalendarSummary;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {

    public CalendarSummary getCalendarSummary(String accessToken) throws Exception {
        HttpRequestInitializer requestInitializer = request ->
                request.getHeaders().setAuthorization("Bearer " + accessToken);

        Calendar calendar = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Burnout Detector")
                .build();

        DateTime twoWeeksAgo = new DateTime(Date.from(
                LocalDateTime.now().minusDays(14).atZone(ZoneId.systemDefault()).toInstant()));
        DateTime now = new DateTime(new Date());

        Events events = calendar.events().list("primary")
                .setTimeMin(twoWeeksAgo)
                .setTimeMax(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return buildSummary(events.getItems());
    }

    CalendarSummary buildSummary(List<Event> events) {
        int totalMeetings = events.size();
        double totalHours = 0;
        int weekendMeetings = 0;
        int backToBackMeetings = 0;
        int lateNightMeetings = 0;
        LocalTime earliestTime = null;
        LocalTime latestTime = null;
        long prevEndMs = -1;
        Map<LocalDate, List<long[]>> meetingsByDay = new LinkedHashMap<>();

        for (Event event : events) {
            if (event.getStart().getDateTime() == null) continue;

            long startMs = event.getStart().getDateTime().getValue();
            long endMs = event.getEnd().getDateTime().getValue();
            totalHours += (endMs - startMs) / 3600000.0;

            if (prevEndMs != -1 && startMs - prevEndMs < 15 * 60 * 1000) {
                backToBackMeetings++;
            }
            prevEndMs = endMs;

            LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startMs), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endMs), ZoneId.systemDefault());

            DayOfWeek day = start.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendMeetings++;
            }

            if (end.getHour() >= 19) {
                lateNightMeetings++;
            }

            meetingsByDay.computeIfAbsent(start.toLocalDate(), k -> new ArrayList<>()).add(new long[]{startMs, endMs});

            LocalTime time = start.toLocalTime();
            if (earliestTime == null || time.isBefore(earliestTime)) earliestTime = time;
            if (latestTime == null || time.isAfter(latestTime)) latestTime = time;
        }

        int noBreakDays = 0;
        for (List<long[]> dayMeetings : meetingsByDay.values()) {
            if (dayMeetings.size() < 2) continue;
            boolean hasBreak = false;
            for (int i = 1; i < dayMeetings.size(); i++) {
                if (dayMeetings.get(i)[0] - dayMeetings.get(i - 1)[1] >= 30 * 60 * 1000) {
                    hasBreak = true;
                    break;
                }
            }
            if (!hasBreak) noBreakDays++;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String earliest = earliestTime != null ? earliestTime.format(formatter) : "-";
        String latest = latestTime != null ? latestTime.format(formatter) : "-";

        return new CalendarSummary(totalMeetings, Math.round(totalHours * 10) / 10.0, earliest, latest, weekendMeetings, backToBackMeetings, lateNightMeetings, noBreakDays);
    }

}