package com.example.swasthyamitra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CurrentMedicineActivity extends AppCompatActivity {

    private EditText editTextMedicationName, editTextDosage, spinnerFrequency, spinnerSchedule, editTextStartDate, editTextEndDate, editTextNotes;
    private Button buttonSave;

    private DatabaseReference currentMedicineRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_medicine);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect user to login screen or handle appropriately
            finish();
        }

        currentMedicineRef = FirebaseDatabase.getInstance().getReference().child("CurrentMedicine").child(currentUser.getUid());

        editTextMedicationName = findViewById(R.id.editTextMedicationName);
        editTextDosage = findViewById(R.id.editTextDosage);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        spinnerSchedule = findViewById(R.id.spinnerSchedule);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextNotes = findViewById(R.id.editTextNotes);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
            }
        });
    }

    private void saveDataToFirebase() {
        String medicationName = editTextMedicationName.getText().toString().trim();
        String dosage = editTextDosage.getText().toString().trim();
        String frequency = spinnerFrequency.getText().toString().trim();
        String schedule = spinnerSchedule.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (medicationName.isEmpty() || dosage.isEmpty() || frequency.isEmpty() || schedule.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String medicineId = currentMedicineRef.push().getKey();
        if (medicineId != null) {
            Medicine medicine = new Medicine(medicineId, medicationName, dosage, frequency, schedule, startDate, endDate, notes);
            currentMedicineRef.child(medicineId).setValue(medicine);
            Toast.makeText(this, "Medicine data saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save medicine data", Toast.LENGTH_SHORT).show();
        }
    }
}
