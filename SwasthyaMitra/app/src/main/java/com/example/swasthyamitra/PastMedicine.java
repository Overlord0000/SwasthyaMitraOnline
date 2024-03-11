package com.example.swasthyamitra;

public class PastMedicine {
    private String medicationName;
    private String dosage;
    private String startDate;
    private String endDate;
    private String notes;
    private String frequency;
    private String schedule;

    // Default constructor (required for Firebase)
    public PastMedicine() {
    }

    public PastMedicine(String medicationName, String dosage, String startDate, String endDate, String notes, String frequency, String schedule) {
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.frequency = frequency;
        this.schedule = schedule;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
