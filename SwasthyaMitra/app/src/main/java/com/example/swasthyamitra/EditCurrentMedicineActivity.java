package com.example.swasthyamitra;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditCurrentMedicineActivity extends AppCompatActivity {

    EditText editTextMedicationName, editTextDosage, editTextFrequency, editTextSchedule, editTextStartDate, editTextEndDate, editTextNotes;
    Button buttonUpdateCurrentMedicine;
    DatabaseReference currentMedRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_current_medicine);

        editTextMedicationName = findViewById(R.id.EdeditTextMedicationName);
        editTextDosage = findViewById(R.id.EdeditTextDosage);
        editTextFrequency = findViewById(R.id.EdFrequency);
        editTextSchedule = findViewById(R.id.EdSchedule);
        editTextStartDate = findViewById(R.id.EdeditTextStartDate);
        editTextEndDate = findViewById(R.id.EdeditTextEndDate);
        editTextNotes = findViewById(R.id.EdeditTextNotes);
        buttonUpdateCurrentMedicine = findViewById(R.id.buttonUpdateCurrentMedicine);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle the case when the user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            String userId = currentUser.getUid();
            currentMedRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("CurrentMedications");

            // Retrieve existing data from the database and populate EditText fields
            currentMedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CurrentMedModel currentMedModel = snapshot.getValue(CurrentMedModel.class);
                        if (currentMedModel != null && currentMedModel.getId().equals(getIntent().getStringExtra("medicationId"))) {
                            editTextMedicationName.setText(currentMedModel.getMedicationName());
                            editTextDosage.setText(String.valueOf(currentMedModel.getDosage()));
                            editTextFrequency.setText(String.valueOf(currentMedModel.getFrequency()));
                            editTextSchedule.setText(currentMedModel.getSchedule());
                            if (currentMedModel.getStartDate() != null) {
                                editTextStartDate.setText(dateFormat.format(currentMedModel.getStartDate()));
                            }
                            if (currentMedModel.getEndDate() != null) {
                                editTextEndDate.setText(dateFormat.format(currentMedModel.getEndDate()));
                            }
                            editTextNotes.setText(currentMedModel.getNotes());
                            break; // Exit the loop after finding the matching medication
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditCurrentMedicineActivity.this, "unable to get medicine details", Toast.LENGTH_SHORT).show();                }
            });


            // Button click listener for updating current medicine
            buttonUpdateCurrentMedicine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCurrentMedicine();
                }
            });

            // Set click listeners for date fields to show date picker dialog
            editTextStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(editTextStartDate);
                }
            });
            editTextEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(editTextEndDate);
                }
            });
        }
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editText.setText(dateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateCurrentMedicine() {
        // Get values from EditText fields
        String medicationName = editTextMedicationName.getText().toString().trim();
        int dosage = Integer.parseInt(editTextDosage.getText().toString().trim());
        int frequency = Integer.parseInt(editTextFrequency.getText().toString().trim());
        String schedule = editTextSchedule.getText().toString().trim();
        String startDateStr = editTextStartDate.getText().toString().trim();
        String endDateStr = editTextEndDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        try {
            // Parse date strings to Date objects
            Date startDate = null;
            if (!startDateStr.isEmpty()) {
                startDate = dateFormat.parse(startDateStr);
            }
            Date endDate = null;
            if (!endDateStr.isEmpty()) {
                endDate = dateFormat.parse(endDateStr);
            }

            // Get medication ID from intent
            String medicationId = getIntent().getStringExtra("medicationId");

            // Create a CurrentMedModel object with updated values
            CurrentMedModel updatedCurrentMedModel = new CurrentMedModel(medicationId, medicationName, dosage, frequency, schedule, startDate, endDate, notes);

            // Update data in the database
            currentMedRef.setValue(updatedCurrentMedModel)
                    .addOnSuccessListener(aVoid -> {
                        // Display success message
                        Toast.makeText(EditCurrentMedicineActivity.this, "Current Medicine Updated Successfully", Toast.LENGTH_SHORT).show();
                        // Finish the activity
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure case
                        Toast.makeText(EditCurrentMedicineActivity.this, "Failed to update current medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (ParseException e) {
            // Handle parse exception
            Toast.makeText(EditCurrentMedicineActivity.this, "Error parsing dates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
