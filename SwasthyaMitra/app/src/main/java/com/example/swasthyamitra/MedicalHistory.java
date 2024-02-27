package com.example.swasthyamitra;

public class MedicalHistory {
    private String existingConditions;
    private String hospitalization;
    private String medications;
    private String allergies;
    private String smoking;
    private String alcoholConsumption;
    private String physicalActivity;
    private String dailyDiet;
    private String restrictions;
    private String lastCheckup;
    private String recentTests;

    public MedicalHistory() {
        // Default constructor required for Firebase
    }

    public MedicalHistory(String existingConditions, String hospitalization, String medications, String allergies, String smoking, String alcoholConsumption, String physicalActivity, String dailyDiet, String restrictions, String lastCheckup, String recentTests) {
        this.existingConditions = existingConditions;
        this.hospitalization = hospitalization;
        this.medications = medications;
        this.allergies = allergies;
        this.smoking = smoking;
        this.alcoholConsumption = alcoholConsumption;
        this.physicalActivity = physicalActivity;
        this.dailyDiet = dailyDiet;
        this.restrictions = restrictions;
        this.lastCheckup = lastCheckup;
        this.recentTests = recentTests;
    }

    public String getExistingConditions() {
        return existingConditions;
    }

    public void setExistingConditions(String existingConditions) {
        this.existingConditions = existingConditions;
    }

    public String getHospitalization() {
        return hospitalization;
    }

    public void setHospitalization(String hospitalization) {
        this.hospitalization = hospitalization;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public String getPhysicalActivity() {
        return physicalActivity;
    }

    public void setPhysicalActivity(String physicalActivity) {
        this.physicalActivity = physicalActivity;
    }

    public String getDailyDiet() {
        return dailyDiet;
    }

    public void setDailyDiet(String dailyDiet) {
        this.dailyDiet = dailyDiet;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getLastCheckup() {
        return lastCheckup;
    }

    public void setLastCheckup(String lastCheckup) {
        this.lastCheckup = lastCheckup;
    }

    public String getRecentTests() {
        return recentTests;
    }

    public void setRecentTests(String recentTests) {
        this.recentTests = recentTests;
    }
}
