package com.example.swasthyamitra;

public class FamilyHistory {
    private String chronicIllnesses;
    private String hereditaryConditions;
    private String heartAttacksStrokes;
    private String highCholesterolBloodPressure;
    private String cancerHistory;
    private String geneticTesting;
    private String neurologicalConditions;

    // Default constructor required for Firebase deserialization
    public FamilyHistory() {
    }

    // Parameterized constructor to initialize fields
    public FamilyHistory(String chronicIllnesses, String hereditaryConditions,
                         String heartAttacksStrokes, String highCholesterolBloodPressure,
                         String cancerHistory, String geneticTesting, String neurologicalConditions) {
        this.chronicIllnesses = chronicIllnesses;
        this.hereditaryConditions = hereditaryConditions;
        this.heartAttacksStrokes = heartAttacksStrokes;
        this.highCholesterolBloodPressure = highCholesterolBloodPressure;
        this.cancerHistory = cancerHistory;
        this.geneticTesting = geneticTesting;
        this.neurologicalConditions = neurologicalConditions;
    }

    // Getters and setters
    public String getChronicIllnesses() {
        return chronicIllnesses;
    }

    public void setChronicIllnesses(String chronicIllnesses) {
        this.chronicIllnesses = chronicIllnesses;
    }

    public String getHereditaryConditions() {
        return hereditaryConditions;
    }

    public void setHereditaryConditions(String hereditaryConditions) {
        this.hereditaryConditions = hereditaryConditions;
    }

    public String getHeartAttacksStrokes() {
        return heartAttacksStrokes;
    }

    public void setHeartAttacksStrokes(String heartAttacksStrokes) {
        this.heartAttacksStrokes = heartAttacksStrokes;
    }

    public String getHighCholesterolBloodPressure() {
        return highCholesterolBloodPressure;
    }

    public void setHighCholesterolBloodPressure(String highCholesterolBloodPressure) {
        this.highCholesterolBloodPressure = highCholesterolBloodPressure;
    }

    public String getCancerHistory() {
        return cancerHistory;
    }

    public void setCancerHistory(String cancerHistory) {
        this.cancerHistory = cancerHistory;
    }

    public String getGeneticTesting() {
        return geneticTesting;
    }

    public void setGeneticTesting(String geneticTesting) {
        this.geneticTesting = geneticTesting;
    }

    public String getNeurologicalConditions() {
        return neurologicalConditions;
    }

    public void setNeurologicalConditions(String neurologicalConditions) {
        this.neurologicalConditions = neurologicalConditions;
    }
}
