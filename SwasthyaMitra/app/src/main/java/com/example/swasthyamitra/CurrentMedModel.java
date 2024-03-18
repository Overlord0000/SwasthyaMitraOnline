package com.example.swasthyamitra;

import java.util.Date;

public class CurrentMedModel {
    private String id; // New field for current medication ID
    private String medicationName;
    private int dosage;
    private int frequency;
    private String schedule;
    private Date startDate;
    private Date endDate;
    private String notes;

    public CurrentMedModel() {
        // Default constructor required for Firebase
    }

    public CurrentMedModel(String id, String medicationName, int dosage, int frequency, String schedule, Date startDate, Date endDate, String notes) {
        this.id = id;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.schedule = schedule;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
