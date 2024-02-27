package com.example.swasthyamitra;

public class Allergy {
    private String id;
    private String allergen;
    private String allergyType;
    private String severity;
    private String symptoms;
    private String triggers;
    private String previousReactions;
    private String treatmentPlan;
    private String medications;

    public Allergy() {
        // Default constructor required for calls to DataSnapshot.getValue(Allergy.class)
    }

    public Allergy(String id, String allergen, String allergyType, String severity, String symptoms, String triggers, String previousReactions, String treatmentPlan, String medications) {
        this.id = id;
        this.allergen = allergen;
        this.allergyType = allergyType;
        this.severity = severity;
        this.symptoms = symptoms;
        this.triggers = triggers;
        this.previousReactions = previousReactions;
        this.treatmentPlan = treatmentPlan;
        this.medications = medications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAllergen() {
        return allergen;
    }

    public void setAllergen(String allergen) {
        this.allergen = allergen;
    }

    public String getAllergyType() {
        return allergyType;
    }

    public void setAllergyType(String allergyType) {
        this.allergyType = allergyType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }

    public String getPreviousReactions() {
        return previousReactions;
    }

    public void setPreviousReactions(String previousReactions) {
        this.previousReactions = previousReactions;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }
}
