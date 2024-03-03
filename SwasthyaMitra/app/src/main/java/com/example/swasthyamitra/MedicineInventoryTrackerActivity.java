package com.example.swasthyamitra;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MedicineInventoryTrackerActivity extends AppCompatActivity {

    private EditText etMedicationName, etDosage, etQuantity, etExpirationDate, etNotes;
    private Button btnSaveMedication;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_inventory_tracker);

        // Initialize views
        etMedicationName = findViewById(R.id.etMedicationName);
        etDosage = findViewById(R.id.etDosage);
        etQuantity = findViewById(R.id.etQuantity);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        etNotes = findViewById(R.id.etNotes);
        btnSaveMedication = findViewById(R.id.btnSaveMedication);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("medications");

        // Set onClickListener for save button
        btnSaveMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedication();
            }
        });


    }



    private void saveMedication() {
        // Get user inputs
        String medicationName = etMedicationName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String expirationDate = etExpirationDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Validate user inputs
        if (medicationName.isEmpty() || dosage.isEmpty() || quantity.isEmpty() || expirationDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create medication object with user inputs
        Map<String, Object> medicationMap = new HashMap<>();
        medicationMap.put("medicationName", medicationName);
        medicationMap.put("dosage", dosage);
        medicationMap.put("quantity", quantity);
        medicationMap.put("expirationDate", expirationDate);
        medicationMap.put("notes", notes);

        // Generate a new key for the medication entry
        String medicationKey = databaseReference.push().getKey();

        // Save medication object to Firebase database
        if (medicationKey != null) {
            databaseReference.child(medicationKey).setValue(medicationMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MedicineInventoryTrackerActivity.this, "Medication saved successfully", Toast.LENGTH_SHORT).show();
                            // Clear input fields after saving
                            clearFields();
                        } else {
                            Toast.makeText(MedicineInventoryTrackerActivity.this, "Failed to save medication", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        etMedicationName.setText("");
        etDosage.setText("");
        etQuantity.setText("");
        etExpirationDate.setText("");
        etNotes.setText("");
    }
}
