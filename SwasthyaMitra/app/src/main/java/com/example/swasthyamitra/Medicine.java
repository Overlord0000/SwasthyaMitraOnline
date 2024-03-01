package com.example.swasthyamitra;

public class Medicine {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String medicationName;

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

    private String dosage;
    private String frequency;
    private String schedule;
    private String startDate;
    private String endDate;
    private String notes;

    public Medicine() {
        // Default constructor required for calls to DataSnapshot.getValue(Medicine.class)
    }

    public Medicine(String id, String medicationName, String dosage, String frequency, String schedule, String startDate, String endDate, String notes) {
        this.id = id;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.schedule = schedule;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }


}
