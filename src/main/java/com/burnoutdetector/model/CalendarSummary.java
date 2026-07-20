package com.burnoutdetector.model;

public class CalendarSummary {

    private int totalMeetings;
    private double totalHours;
    private String earliestMeeting;
    private String latestMeeting;
    private int weekendMeetings;
    private int backToBackMeetings;
    private int lateNightMeetings;
    private int noBreakDays;

    public CalendarSummary(int totalMeetings, double totalHours, String earliestMeeting, String latestMeeting, int weekendMeetings, int backToBackMeetings, int lateNightMeetings, int noBreakDays) {
        this.totalMeetings = totalMeetings;
        this.totalHours = totalHours;
        this.earliestMeeting = earliestMeeting;
        this.latestMeeting = latestMeeting;
        this.weekendMeetings = weekendMeetings;
        this.backToBackMeetings = backToBackMeetings;
        this.lateNightMeetings = lateNightMeetings;
        this.noBreakDays = noBreakDays;
    }

    public int getTotalMeetings() { return totalMeetings; }
    public double getTotalHours() { return totalHours; }
    public String getEarliestMeeting() { return earliestMeeting; }
    public String getLatestMeeting() { return latestMeeting; }
    public int getWeekendMeetings() { return weekendMeetings; }
    public int getBackToBackMeetings() { return backToBackMeetings; }
    public int getLateNightMeetings() { return lateNightMeetings; }
    public int getNoBreakDays() { return noBreakDays; }

}