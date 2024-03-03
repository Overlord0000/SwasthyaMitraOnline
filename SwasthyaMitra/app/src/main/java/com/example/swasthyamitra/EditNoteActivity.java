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

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private MaterialButton saveBtn;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;

    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctors_note);

        titleInput = findViewById(R.id.titleinput_edit);
        descriptionInput = findViewById(R.id.descriptioninput_edit);
        saveBtn = findViewById(R.id.savebtn_edit);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid()).child("DoctorNotes");

        noteId = getIntent().getStringExtra("noteId");

        populateNoteDetails();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });
    }

    private void populateNoteDetails() {
        databaseRef.child(noteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    titleInput.setText(title);
                    descriptionInput.setText(description);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

        DatabaseReference userNoteRef = databaseRef.child(noteId);
        userNoteRef.child("title").setValue(title);
        userNoteRef.child("description").setValue(description);

        Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
