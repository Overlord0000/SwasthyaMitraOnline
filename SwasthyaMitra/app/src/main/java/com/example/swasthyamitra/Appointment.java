package com.example.swasthyamitra;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {

    private String id;
    private String title;
    private long dateInMillis;
    private long timeInMillis; // Added for consistency with time representation
    private String location;
    private int hoursBefore;

    // Constructor to create Appointment from date and time in milliseconds
    public Appointment(String id, String title, long dateInMillis, long timeInMillis, String location, int hoursBefore) {
        this.id = id;
        this.title = title;
        this.dateInMillis = dateInMillis;
        this.timeInMillis = timeInMillis;
        this.location = location;
        this.hoursBefore = hoursBefore;
    }
    public Appointment() {
        // Default constructor required for Firebase
    }

    // Getters and setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getHoursBefore() {
        return hoursBefore;
    }

    public void setHoursBefore(int hoursBefore) {
        this.hoursBefore = hoursBefore;
    }

    public long getDateInMillis(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getTimeInMillis(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return sdf.parse(timeStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
