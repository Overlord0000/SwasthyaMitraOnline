package com.example.swasthyamitra;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMedicalHistoryActivity extends AppCompatActivity {

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
    private Button buttonUpdate;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserMedicalHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medical_history);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mUserMedicalHistoryRef = mDatabase.getReference().child("MedicalHistory").child(userId).child("medical_history");

            // Retrieve medical history data from Firebase
            mUserMedicalHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Medical history data exists, populate the UI fields
                        MedicalHistory medicalHistory = dataSnapshot.getValue(MedicalHistory.class);
                        if (medicalHistory != null) {
                            // Populate EditText fields
                            existingMedicalConditions.setText(medicalHistory.getExistingConditions());
                            hospitalizationDetails.setText(medicalHistory.getHospitalization());
                            currentMedications.setText(medicalHistory.getMedications());
                            knownAllergies.setText(medicalHistory.getAllergies());
                            typicalDailyDiet.setText(medicalHistory.getDailyDiet());
                            dietaryRestrictions.setText(medicalHistory.getRestrictions());
                            lastMedicalCheckup.setText(medicalHistory.getLastCheckup());
                            recentMedicalTests.setText(medicalHistory.getRecentTests());

                            // Set selected RadioButtons in RadioGroup
                            String smokingStatus = medicalHistory.getSmoking();
                            if (smokingStatus.equals(getString(R.string.yes))) {
                                radioGroupSmoking.check(R.id.EdradioSmokingYes);
                            } else if (smokingStatus.equals(getString(R.string.no))) {
                                radioGroupSmoking.check(R.id.EdradioSmokingNo);
                            }

                            // Set selected items in Spinners
                            setSpinnerItemSelectedByValue(spinnerAlcoholConsumption, medicalHistory.getAlcoholConsumption());
                            setSpinnerItemSelectedByValue(spinnerPhysicalActivity, medicalHistory.getPhysicalActivity());
                        }
                    } else {
                        // Medical history data does not exist
                        Toast.makeText(EditMedicalHistoryActivity.this, "Medical history data does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                    Log.e("EditMedicalHistory", "Error reading medical history data", databaseError.toException());
                }
            });
        }

        existingMedicalConditions = findViewById(R.id.EdExistingMedicalConditions);
        hospitalizationDetails = findViewById(R.id.EdHospitalizationDetails);
        currentMedications = findViewById(R.id.EdCurrentMedications);
        knownAllergies = findViewById(R.id.EdKnownAllergies);
        radioGroupSmoking = findViewById(R.id.radioGroupSmoking);
        spinnerAlcoholConsumption = findViewById(R.id.EdspinnerAlcoholConsumption);
        spinnerPhysicalActivity = findViewById(R.id.EdspinnerPhysicalActivity);
        typicalDailyDiet = findViewById(R.id.EdTypicalDailyDiet);
        dietaryRestrictions = findViewById(R.id.EdDietaryRestrictions);
        lastMedicalCheckup = findViewById(R.id.EdLastMedicalCheckup);
        recentMedicalTests = findViewById(R.id.EdRecentMedicalTests);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMedicalHistory();
            }
        });
    }

    private void updateMedicalHistory() {
        String existingConditions = existingMedicalConditions.getText().toString();
        String hospitalization = hospitalizationDetails.getText().toString();
        String medications = currentMedications.getText().toString();
        String allergies = knownAllergies.getText().toString();
        String smoking = getSelectedRadioButtonText(radioGroupSmoking);
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
                    Toast.makeText(EditMedicalHistoryActivity.this, "Medical history updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving data
                    Toast.makeText(EditMedicalHistoryActivity.this, "Failed to update medical history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton radioButton = findViewById(selectedRadioButtonId);
            return radioButton.getText().toString();
        } else {
            return "";
        }
    }

    // Helper method to set the selected item in a Spinner by value
    private void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}
