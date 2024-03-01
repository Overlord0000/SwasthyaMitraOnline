package com.example.swasthyamitra;

public class UserProfile {
    private String name;
    private String age;
    private String dob;
    private String emergencyContact;
    private String address;
    private String bloodGroup;
    private String gender;
    private String profilePicture;

    public UserProfile() {
        // Default constructor required for Firebase
    }

    public UserProfile(String name, String age, String dob, String emergencyContact, String address, String bloodGroup, String gender, String profilePicture) {
        this.name = name;
        this.age = age;
        this.dob = dob;
        this.emergencyContact = emergencyContact;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.gender = gender;
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
