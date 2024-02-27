package com.example.swasthyamitra;

import java.util.Date;

public class Appointment {
    private String title;
    private Date date;
    private String time;
    private String location;
    private int reminderMinutes;

    public Appointment(String appointmentTitle, String date, String time, String location, int minutesBefore) {
        // Default constructor required for Firebase
    }

    public Appointment(String title, Date date, String time, String location, int reminderMinutes) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.reminderMinutes = reminderMinutes;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getReminderMinutes() {
        return reminderMinutes;
    }

    public void setReminderMinutes(int reminderMinutes) {
        this.reminderMinutes = reminderMinutes;
    }
}
