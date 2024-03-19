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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditPastMedicineActivity extends AppCompatActivity {

    private EditText editTextMedicationNameP, editTextDosageP, editTextStartDateP, editTextEndDateP, editTextNotesP;
    private EditText editTextFrequencyPM, editTextSchedulePM;
    private Button buttonUpdateP;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String pastMedicineId;

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
        editTextFrequencyPM = findViewById(R.id.ededitTextFrequencyPM);
        editTextSchedulePM = findViewById(R.id.ededitTextSchedulePM);
        buttonUpdateP = findViewById(R.id.buttonUpdateP);

        // Retrieve past medicine ID from intent
        pastMedicineId = getIntent().getStringExtra("pastMedicineId");

        // Fetch and display past medicine data
        fetchPastMedicineData();

        buttonUpdateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePastMedicine();
            }
        });
    }

    private void fetchPastMedicineData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).child("PastMedications").child(pastMedicineId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        PastMedicine pastMedicine = dataSnapshot.getValue(PastMedicine.class);
                        if (pastMedicine != null) {
                            // Set retrieved data to the UI elements
                            editTextMedicationNameP.setText(pastMedicine.getMedicationName());
                            editTextDosageP.setText(String.valueOf(pastMedicine.getDosage()));
                            editTextStartDateP.setText(formatDate(pastMedicine.getStartDate()));
                            editTextEndDateP.setText(formatDate(pastMedicine.getEndDate()));
                            editTextNotesP.setText(pastMedicine.getNotes());
                            editTextFrequencyPM.setText(String.valueOf(pastMedicine.getFrequency()));
                            editTextSchedulePM.setText(pastMedicine.getSchedule());
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
            int dosage = Integer.parseInt(editTextDosageP.getText().toString().trim());
            String startDate = editTextStartDateP.getText().toString().trim();
            String endDate = editTextEndDateP.getText().toString().trim();
            String notes = editTextNotesP.getText().toString().trim();
            int frequency = Integer.parseInt(editTextFrequencyPM.getText().toString().trim());
            String schedule = editTextSchedulePM.getText().toString().trim();

            try {
                // Convert startDate and endDate strings to Date objects
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Date startDateValue = dateFormat.parse(startDate);
                Date endDateValue = dateFormat.parse(endDate);

                // Create a PastMedicine object
                PastMedicine pastMedicine = new PastMedicine(pastMedicineId, medicationName, dosage, frequency, schedule, startDateValue, endDateValue, notes);

                // Save to Firebase
                mDatabase.child("users").child(userId).child("PastMedications").child(pastMedicineId).setValue(pastMedicine)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditPastMedicineActivity.this, "Past medicine updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditPastMedicineActivity.this, "Failed to update past medicine", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (ParseException e) {
                Toast.makeText(EditPastMedicineActivity.this, "Error parsing dates", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditPastMedicineActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to format Date object to string
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(date);
    }
}
