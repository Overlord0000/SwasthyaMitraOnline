package com.example.swasthyamitra;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private DatabaseReference mDatabase;
    private NoteAdapter mAdapter;

    public FirebaseDatabaseHelper(NoteAdapter adapter) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference("notes");
        this.mAdapter = adapter;
    }

    // Add a new note to the database
    public void addNote(Note note) {
        String key = mDatabase.push().getKey();
        if (key != null) {
            mDatabase.child(key).setValue(note);
        }
    }

    // Retrieve all notes from the database
    public void getAllNotes() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Note> notes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    if (note != null) {
                        notes.add(note);
                    }
                }
                mAdapter.setNotes(notes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Update an existing note in the database
    public void updateNote(String key, Note note) {
        mDatabase.child(key).setValue(note);
    }

    // Delete a note from the database
    public void deleteNote(String key) {
        mDatabase.child(key).removeValue();
    }
}
