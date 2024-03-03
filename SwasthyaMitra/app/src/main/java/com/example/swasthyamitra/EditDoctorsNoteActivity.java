package com.example.swasthyamitra;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditDoctorsNoteActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private MaterialButton saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference notesRef;
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctors_note);

        titleInput = findViewById(R.id.titleinput_edit);
        descriptionInput = findViewById(R.id.descriptioninput_edit);
        saveButton = findViewById(R.id.savebtn_edit);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login activity or handle authentication
            // based on your app's requirements
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            String userId = currentUser.getUid();
            notesRef = FirebaseDatabase.getInstance().getReference("DoctorsNote").child(userId).child("notes");

            // Get the note ID passed from MainActivity
            noteId = getIntent().getStringExtra("noteId");

            if (noteId == null) {
                // Handle the case where noteId is null (optional)
                Toast.makeText(this, "Note ID is null", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Retrieve the existing note data from Firebase Realtime Database
            notesRef.child(noteId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        NoteModel note = dataSnapshot.getValue(NoteModel.class);
                        if (note != null) {
                            // Populate EditText fields with existing note data
                            titleInput.setText(note.getTitle());
                            descriptionInput.setText(note.getDescription());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditDoctorsNoteActivity.this, "Failed to fetch note data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });
    }


    private void updateNote() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the note in Firebase Realtime Database
        notesRef.child(noteId).child("title").setValue(title);
        notesRef.child(noteId).child("description").setValue(description);

        Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
