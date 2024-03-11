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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPastMedicineActivity extends AppCompatActivity {

    private EditText editTextMedicationNameP, editTextDosageP, editTextStartDateP, editTextEndDateP, editTextNotesP;
    private Spinner spinnerFrequencyP, spinnerScheduleP;
    private Button buttonUpdateP;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_past_medicine);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextMedicationNameP = findViewById(R.id.EdeditTextMedicationNameP);
        editTextDosageP = findViewById(R.id.EdeditTextDosageP);
        editTextStartDateP = findViewById(R.id.EdeditTextStartDateP);
        editTextEndDateP = findViewById(R.id.EdeditTextEndDateP);
        editTextNotesP = findViewById(R.id.EdeditTextNotesP);
        spinnerFrequencyP = findViewById(R.id.EdspinnerFrequencyP);
        spinnerScheduleP = findViewById(R.id.EdspinnerScheduleP);
        buttonUpdateP = findViewById(R.id.buttonUpdateP);

        buttonUpdateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePastMedicine();
            }
        });

        // Retrieve past medicine data
        retrievePastMedicineData();
    }

    private void retrievePastMedicineData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).child("past_medicine").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        PastMedicine pastMedicine = dataSnapshot.getValue(PastMedicine.class);
                        if (pastMedicine != null) {
                            // Set retrieved data to the UI elements
                            editTextMedicationNameP.setText(pastMedicine.getMedicationName());
                            editTextDosageP.setText(pastMedicine.getDosage());
                            editTextStartDateP.setText(pastMedicine.getStartDate());
                            editTextEndDateP.setText(pastMedicine.getEndDate());
                            editTextNotesP.setText(pastMedicine.getNotes());


                            spinnerFrequencyP.setSelection(getIndex(spinnerFrequencyP, pastMedicine.getFrequency()));
                             spinnerScheduleP.setSelection(getIndex(spinnerScheduleP, pastMedicine.getSchedule()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditPastMedicineActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updatePastMedicine() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            String medicationName = editTextMedicationNameP.getText().toString().trim();
            String dosage = editTextDosageP.getText().toString().trim();
            String startDate = editTextStartDateP.getText().toString().trim();
            String endDate = editTextEndDateP.getText().toString().trim();
            String notes = editTextNotesP.getText().toString().trim();
            String frequency = spinnerFrequencyP.getSelectedItem().toString();
            String schedule = spinnerScheduleP.getSelectedItem().toString();

            // Create a PastMedicine object
            PastMedicine pastMedicine = new PastMedicine(medicationName, dosage, startDate, endDate, notes, frequency, schedule);

            // Save to Firebase
            mDatabase.child("users").child(userId).child("past_medicine").setValue(pastMedicine)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditPastMedicineActivity.this, "Past medicine updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditPastMedicineActivity.this, "Failed to update past medicine", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditPastMedicineActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to get the index of an item in the spinner
    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0; // Default to the first item if not found
    }
}
