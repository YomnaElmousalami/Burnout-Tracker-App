package com.burnoutdetector.demo.model;

public class CalendarSummary {

    private int totalMeetings;
    private double totalHours;
    private String earliestMeeting;
    private String latestMeeting;
    private int weekendMeetings;

    public CalendarSummary(int totalMeetings, double totalHours, String earliestMeeting, String latestMeeting, int weekendMeetings) {
        this.totalMeetings = totalMeetings;
        this.totalHours = totalHours;
        this.earliestMeeting = earliestMeeting;
        this.latestMeeting = latestMeeting;
        this.weekendMeetings = weekendMeetings;
    }

    public int getTotalMeetings() { return totalMeetings; }
    public double getTotalHours() { return totalHours; }
    public String getEarliestMeeting() { return earliestMeeting; }
    public String getLatestMeeting() { return latestMeeting; }
    public int getWeekendMeetings() { return weekendMeetings; }

}