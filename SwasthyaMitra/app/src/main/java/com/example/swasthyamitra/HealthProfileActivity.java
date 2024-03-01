package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HealthProfileActivity extends AppCompatActivity {

    private ImageButton UserInfobtn, MedicalHistorybtn, Allergiesbtn, FamilyHistorybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_profile);

        UserInfobtn = findViewById(R.id.UserInfobtn);
        MedicalHistorybtn = findViewById(R.id.MedicalHistorybtn);
        Allergiesbtn = findViewById(R.id.Allergiesbtn);
        FamilyHistorybtn = findViewById(R.id.FamilyHistorybtn);

        UserInfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current user's ID
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Check if user data exists for the current user in Firebase Realtime Database
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                            .child("UserProfile").child(userId);

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // If the dataSnapshot exists, it means user data exists for the current user
                            if (dataSnapshot.exists()) {

                                Intent intent = new Intent(getApplicationContext(), EditUserProfileActivity.class);
                                startActivity(intent);
                            } else {
                                // User data does not exist
                                // Proceed to UserProfileActivity
                                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle potential errors
                            Log.e("HealthProfileActivity", "Error reading user data", databaseError.toException());
                            // Since we couldn't determine if user data exists or not, navigate to UserProfileActivity by default
                            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });


        MedicalHistorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the current user has medical history data
                checkAndNavigateToMedicalHistory();
            }
        });

        Allergiesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllergiesActivity.class);
                startActivity(intent);
            }
        });

        FamilyHistorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndNavigateToFamilyHistory();

            }
        });
    }

    private void checkAndNavigateToMedicalHistory() {
        // Get the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Check if the medical history data node exists for the current user in Firebase Realtime Database
            DatabaseReference medicalHistoryRef = FirebaseDatabase.getInstance().getReference()
                    .child("MedicalHistory").child(userId).child("medical_history");

            medicalHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // If the dataSnapshot exists, it means the medical history data exists for the current user
                    if (dataSnapshot.exists()) {
                        // Medical history data exists
                        // Proceed to EditMedicalHistoryActivity
                        Intent intent = new Intent(getApplicationContext(), EditMedicalHistoryActivity.class);
                        startActivity(intent);
                    } else {
                        // Medical history data does not exist
                        // Proceed to MedicalHistoryActivity
                        Intent intent = new Intent(getApplicationContext(), MedicalHistoryActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                    Log.e("MedicalHistoryActivity", "Error reading medical history data", databaseError.toException());
                    // Since we couldn't determine if medical history data exists or not, navigate to MedicalHistoryActivity by default
                    Intent intent = new Intent(getApplicationContext(), MedicalHistoryActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void checkAndNavigateToFamilyHistory() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Check if the family history data node exists for the current user in Firebase Realtime Database
            DatabaseReference FamilyHistoryRef = FirebaseDatabase.getInstance().getReference()
                    .child("FamilyMedicalHistory").child(userId).child("Family_History");

            FamilyHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // If the dataSnapshot exists, it means the medical history data exists for the current user
                    if (dataSnapshot.exists()) {
                        // Family history data exists
                        // Proceed to EditMedicalHistoryActivity
                        Intent intent = new Intent(getApplicationContext(), EditFamilyHistoryActivity.class);
                        startActivity(intent);
                    } else {
                        // Medical history data does not exist
                        // Proceed to MedicalHistoryActivity
                        Intent intent = new Intent(getApplicationContext(), FamilyHistoryActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                    Log.e("FamilyHistoryActivity", "Error reading family history data", databaseError.toException());
                    // Since we couldn't determine if medical history data exists or not, navigate to MedicalHistoryActivity by default
                    //Intent intent = new Intent(getApplicationContext(), FamilyHistoryActivity.class);
                    //startActivity(intent);
                }
            });
        }
    }
}
