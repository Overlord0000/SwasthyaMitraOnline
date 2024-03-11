package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrescriptionActivity extends AppCompatActivity {

    private ImageButton currentMedicineBtn, pastMedicineBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        currentMedicineBtn = findViewById(R.id.CurrentMedicinebtn);
        pastMedicineBtn = findViewById(R.id.PastMedicinebtn);

        // Set click listeners
        currentMedicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open CurrentMedicineActivity
                startActivity(new Intent(PrescriptionActivity.this, CurrentMedicineActivity.class));
            }
        });

        pastMedicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if past_medicine data exists for the current user
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    mDatabase.child("users").child(userId).child("past_medicine").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Past medicine data exists, open EditPastMedicineActivity
                                startActivity(new Intent(PrescriptionActivity.this, EditPastMedicineActivity.class));
                            } else {
                                // Past medicine data does not exist, open PastMedicineActivity
                                startActivity(new Intent(PrescriptionActivity.this, PastMedineActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(PrescriptionActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PrescriptionActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
