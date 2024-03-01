package com.example.swasthyamitra;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private MaterialButton saveBtn;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;

    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        titleInput = findViewById(R.id.titleinput_edit);
        descriptionInput = findViewById(R.id.descriptioninput_edit);
        saveBtn = findViewById(R.id.savebtn_edit);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("DoctorsNotes");

        // Get noteId from intent extras
        noteId = getIntent().getStringExtra("noteId");

        // Fetch note details and populate EditTexts
        populateNoteDetails();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });
    }

    private void populateNoteDetails() {
        // Get the reference to the specific note using noteId
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userNoteRef = databaseRef.child(userId).child(noteId);

        // Fetch note details from the database
        userNoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve note data
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);

                    // Populate EditTexts with note data
                    titleInput.setText(title);
                    descriptionInput.setText(description);
                } else {
                    // Handle case where note does not exist
                    Toast.makeText(EditNoteActivity.this, "Note not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
                Toast.makeText(EditNoteActivity.this, "Failed to retrieve note details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateNote() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userNoteRef = databaseRef.child(userId).child(noteId);
        userNoteRef.child("title").setValue(title);
        userNoteRef.child("description").setValue(description);

        Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
