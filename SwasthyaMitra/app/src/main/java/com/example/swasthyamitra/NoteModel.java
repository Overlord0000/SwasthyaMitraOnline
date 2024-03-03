package com.example.swasthyamitra;

import java.util.Date;

public class NoteModel {
    private String id; // New field for note ID
    private String title;
    private String description;
    private Date creationDate;

    public NoteModel() {
        // Default constructor required for Firebase
    }

    public NoteModel(String id, String title, String description, Date creationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
