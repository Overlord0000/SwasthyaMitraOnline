package com.example.swasthyamitra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicalHistoryActivity extends AppCompatActivity {

    private EditText existingMedicalConditions;
    private EditText hospitalizationDetails;
    private EditText currentMedications;
    private EditText knownAllergies;
    private RadioGroup radioGroupSmoking;
    private Spinner spinnerAlcoholConsumption;
    private Spinner spinnerPhysicalActivity;
    private EditText typicalDailyDiet;
    private EditText dietaryRestrictions;
    private EditText lastMedicalCheckup;
    private EditText recentMedicalTests;
    private Button buttonSave;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserMedicalHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mUserMedicalHistoryRef = mDatabase.getReference().child("users").child(userId).child("medical_history");
        }

        existingMedicalConditions = findViewById(R.id.ExistingMedicalConditions);
        hospitalizationDetails = findViewById(R.id.HospitalizationDetails);
        currentMedications = findViewById(R.id.CurrentMedications);
        knownAllergies = findViewById(R.id.KnownAllergies);
        radioGroupSmoking = findViewById(R.id.radioGroupSmoking);
        spinnerAlcoholConsumption = findViewById(R.id.spinnerAlcoholConsumption);
        spinnerPhysicalActivity = findViewById(R.id.spinnerPhysicalActivity);
        typicalDailyDiet = findViewById(R.id.TypicalDailyDiet);
        dietaryRestrictions = findViewById(R.id.DietaryRestrictions);
        lastMedicalCheckup = findViewById(R.id.LastMedicalCheckup);
        recentMedicalTests = findViewById(R.id.RecentMedicalTests);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicalHistory();
            }
        });
    }

    private void saveMedicalHistory() {
        String existingConditions = existingMedicalConditions.getText().toString();
        String hospitalization = hospitalizationDetails.getText().toString();
        String medications = currentMedications.getText().toString();
        String allergies = knownAllergies.getText().toString();
        String smoking = ((RadioButton) findViewById(radioGroupSmoking.getCheckedRadioButtonId())).getText().toString();
        String alcoholConsumption = spinnerAlcoholConsumption.getSelectedItem().toString();
        String physicalActivity = spinnerPhysicalActivity.getSelectedItem().toString();
        String dailyDiet = typicalDailyDiet.getText().toString();
        String restrictions = dietaryRestrictions.getText().toString();
        String lastCheckup = lastMedicalCheckup.getText().toString();
        String recentTests = recentMedicalTests.getText().toString();

        // Create a MedicalHistory object
        MedicalHistory medicalHistory = new MedicalHistory(
                existingConditions,
                hospitalization,
                medications,
                allergies,
                smoking,
                alcoholConsumption,
                physicalActivity,
                dailyDiet,
                restrictions,
                lastCheckup,
                recentTests
        );

        // Push the medical history data to Firebase Realtime Database
        mUserMedicalHistoryRef.setValue(medicalHistory)
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully
                    Toast.makeText(MedicalHistoryActivity.this, "Medical history saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving data
                    Toast.makeText(MedicalHistoryActivity.this, "Failed to save medical history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
