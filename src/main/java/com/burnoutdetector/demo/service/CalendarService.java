package com.burnoutdetector.demo.service;

import com.burnoutdetector.demo.model.CalendarSummary;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;
import java.util.List;

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

    private CalendarSummary buildSummary(List<Event> events) {
        int totalMeetings = events.size();
        double totalHours = 0;
        int weekendMeetings = 0;
        LocalTime earliestTime = null;
        LocalTime latestTime = null;

        for (Event event : events) {
            if (event.getStart().getDateTime() == null) continue;

            long startMs = event.getStart().getDateTime().getValue();
            long endMs = event.getEnd().getDateTime().getValue();
            totalHours += (endMs - startMs) / 3600000.0;

            LocalDateTime start = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(startMs), ZoneId.systemDefault());

            DayOfWeek day = start.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendMeetings++;
            }

            LocalTime time = start.toLocalTime();
            if (earliestTime == null || time.isBefore(earliestTime)) earliestTime = time;
            if (latestTime == null || time.isAfter(latestTime)) latestTime = time;
        }

        String earliest = earliestTime != null ? String.format("%02d:%02d", earliestTime.getHour(), earliestTime.getMinute()) : "-";
        String latest = latestTime != null ? String.format("%02d:%02d", latestTime.getHour(), latestTime.getMinute()) : "-";

        return new CalendarSummary(totalMeetings, Math.round(totalHours * 10) / 10.0, earliest, latest, weekendMeetings);
    }

}