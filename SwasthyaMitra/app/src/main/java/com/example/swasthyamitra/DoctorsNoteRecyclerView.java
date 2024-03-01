package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DoctorsNoteRecyclerView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter NoteAdapter;
    private List<Note> noteList;
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_note_recycler_view);

        // Initialize Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("notes");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteList = new ArrayList<>();
        NoteAdapter = new NoteAdapter(this, noteList);
        recyclerView.setAdapter(NoteAdapter);

        // Load notes from Firebase
        loadNotes();

        // Set click listener for Add New Note button
        MaterialButton addNoteBtn = findViewById(R.id.addnewnotebtn);
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DoctorsNoteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadNotes() {
        // Check if user is authenticated
        if (currentUser != null) {
            // Retrieve notes from Firebase Realtime Database
            databaseRef.child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    Note note = dataSnapshot.getValue(Note.class);
                    noteList.add(note);
                    NoteAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    // Implement if you want to handle note updates
                    String noteId = dataSnapshot.getKey();
                    Note updatedNote = dataSnapshot.getValue(Note.class);

                    // Iterate through the noteList to find the note to update
                    for (int i = 0; i < noteList.size(); i++) {
                        Note note = noteList.get(i);
                        if (note.getId().equals(noteId)) {
                            // Update the note in the list
                            noteList.set(i, updatedNote);
                            // Notify the adapter that the data has changed
                            NoteAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    // Implement if you want to handle note deletions
                    String noteId = dataSnapshot.getKey();

                    // Iterate through the noteList to find the note to remove
                    for (int i = 0; i < noteList.size(); i++) {
                        Note note = noteList.get(i);
                        if (note.getId().equals(noteId)) {
                            // Remove the note from the list
                            noteList.remove(i);
                            // Notify the adapter that an item has been removed
                            NoteAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }


                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    // Implement if you want to handle changes in note order
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DoctorsNoteRecyclerView.this, "unable to reach firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
