package com.example.swasthyamitra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PastMedineActivity extends AppCompatActivity {

    private EditText editTextMedicationNameP, editTextDosageP, editTextStartDateP, editTextEndDateP, editTextNotesP;
    private Spinner spinnerFrequencyP, spinnerScheduleP;
    private Button buttonSaveP;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference pastMedicineRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_medine);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect user to login screen or handle appropriately
            finish();
        }

        pastMedicineRef = mDatabase.getReference().child("past_medicines").child(currentUser.getUid());

        // Initialize views
        editTextMedicationNameP = findViewById(R.id.editTextMedicationNameP);
        editTextDosageP = findViewById(R.id.editTextDosageP);
        editTextStartDateP = findViewById(R.id.editTextStartDateP);
        editTextEndDateP = findViewById(R.id.editTextEndDateP);
        editTextNotesP = findViewById(R.id.editTextNotesP);
        spinnerFrequencyP = findViewById(R.id.spinnerFrequencyP);
        spinnerScheduleP = findViewById(R.id.spinnerScheduleP);
        buttonSaveP = findViewById(R.id.buttonSaveP);

        // Set click listener for the save button
        buttonSaveP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePastMedicine();
            }
        });
    }

    private void savePastMedicine() {
        // Get user input values
        String medicationName = editTextMedicationNameP.getText().toString().trim();
        String dosage = editTextDosageP.getText().toString().trim();
        String startDate = editTextStartDateP.getText().toString().trim();
        String endDate = editTextEndDateP.getText().toString().trim();
        String notes = editTextNotesP.getText().toString().trim();
        String frequency = spinnerFrequencyP.getSelectedItem().toString();
        String schedule = spinnerScheduleP.getSelectedItem().toString();

        // Validate input
        if (medicationName.isEmpty() || dosage.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store past medicine data
        Map<String, Object> pastMedicineMap = new HashMap<>();
        pastMedicineMap.put("medicationName", medicationName);
        pastMedicineMap.put("dosage", dosage);
        pastMedicineMap.put("startDate", startDate);
        pastMedicineMap.put("endDate", endDate);
        pastMedicineMap.put("notes", notes);
        pastMedicineMap.put("frequency", frequency);
        pastMedicineMap.put("schedule", schedule);

        // Save data to Firebase
        pastMedicineRef.push().setValue(pastMedicineMap);

        // Display success message
        Toast.makeText(this, "Past medicine saved successfully", Toast.LENGTH_SHORT).show();

        // Clear input fields
        editTextMedicationNameP.setText("");
        editTextDosageP.setText("");
        editTextStartDateP.setText("");
        editTextEndDateP.setText("");
        editTextNotesP.setText("");
    }
}
