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
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_medine);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextMedicationNameP = findViewById(R.id.editTextMedicationNameP);
        editTextDosageP = findViewById(R.id.editTextDosageP);
        editTextStartDateP = findViewById(R.id.editTextStartDateP);
        editTextEndDateP = findViewById(R.id.editTextEndDateP);
        editTextNotesP = findViewById(R.id.editTextNotesP);
        spinnerFrequencyP = findViewById(R.id.spinnerFrequencyP);
        spinnerScheduleP = findViewById(R.id.spinnerScheduleP);

        buttonSaveP = findViewById(R.id.buttonSaveP);
        buttonSaveP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicationData();
            }
        });
    }

    private void saveMedicationData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            String medicationName = editTextMedicationNameP.getText().toString().trim();
            String dosage = editTextDosageP.getText().toString().trim();
            String frequency = spinnerFrequencyP.getSelectedItem().toString();
            String schedule = spinnerScheduleP.getSelectedItem().toString();
            String startDate = editTextStartDateP.getText().toString().trim();
            String endDate = editTextEndDateP.getText().toString().trim();
            String notes = editTextNotesP.getText().toString().trim();

            Map<String, Object> medicationData = new HashMap<>();
            medicationData.put("medicationName", medicationName);
            medicationData.put("dosage", dosage);
            medicationData.put("frequency", frequency);
            medicationData.put("schedule", schedule);
            medicationData.put("startDate", startDate);
            medicationData.put("endDate", endDate);
            medicationData.put("notes", notes);

            mDatabase.child("users").child(userId).child("pastMedications").push().setValue(medicationData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PastMedineActivity.this, "Medication data saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PastMedineActivity.this, "Failed to save medication data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(PastMedineActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
