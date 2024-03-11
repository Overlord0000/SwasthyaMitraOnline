package com.example.swasthyamitra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.HashMap;
import java.util.Map;

public class EditAllergiesActivity extends AppCompatActivity {
    private EditText editTextAllergen, editTextSymptoms, editTextTriggers, editTextPreviousReactions, editTextTreatmentPlan, editTextMedications;
    private CheckBox checkBoxFood, checkBoxDrug, checkBoxEnvironmental, checkBoxInsect, checkBoxOther;
    private Spinner spinnerSeverity;
    private Button buttonUpdateAllergy;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String allergyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_allergies);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextAllergen = findViewById(R.id.EdeditTextAllergen);
        editTextSymptoms = findViewById(R.id.EdeditTextSymptoms);
        editTextTriggers = findViewById(R.id.EdeditTextTriggers);
        editTextPreviousReactions = findViewById(R.id.EdeditTextPreviousReactions);
        editTextTreatmentPlan = findViewById(R.id.EdeditTextTreatmentPlan);
        editTextMedications = findViewById(R.id.EdeditTextMedications);

        checkBoxFood = findViewById(R.id.EdcheckBoxFood);
        checkBoxDrug = findViewById(R.id.EdcheckBoxDrug);
        checkBoxEnvironmental = findViewById(R.id.EdcheckBoxEnvironmental);
        checkBoxInsect = findViewById(R.id.EdcheckBoxInsect);
        checkBoxOther = findViewById(R.id.checkBoxOther);

        spinnerSeverity = findViewById(R.id.EdspinnerSeverity);

        buttonUpdateAllergy = findViewById(R.id.buttonUpdateAllergy);
        buttonUpdateAllergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllergy();
            }
        });

        // Retrieve data from Realtime Database and populate the fields
        retrieveAllergyData();
    }

    private void retrieveAllergyData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child("allergies").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Clear the views before populating new data

                    // Iterate through the allergy data and populate the views
                    for (DataSnapshot allergySnapshot : dataSnapshot.getChildren()) {
                        String allergen = allergySnapshot.child("allergen").getValue(String.class);
                        String symptoms = allergySnapshot.child("symptoms").getValue(String.class);
                        String triggers = allergySnapshot.child("triggers").getValue(String.class);
                        String previousReactions = allergySnapshot.child("previousReactions").getValue(String.class);
                        String treatmentPlan = allergySnapshot.child("treatmentPlan").getValue(String.class);
                        String medications = allergySnapshot.child("medications").getValue(String.class);
                        String severity = allergySnapshot.child("severity").getValue(String.class);
                        allergyId = allergySnapshot.getKey(); // Get the allergyId

                        // Populate the views with the retrieved data
                        editTextAllergen.setText(allergen);
                        editTextSymptoms.setText(symptoms);
                        editTextTriggers.setText(triggers);
                        editTextPreviousReactions.setText(previousReactions);
                        editTextTreatmentPlan.setText(treatmentPlan);
                        editTextMedications.setText(medications);
                        // Set the selected item in the spinner based on the severity value

                        // Get the allergy types map
                        Map<String, Boolean> allergyTypes = (Map<String, Boolean>) allergySnapshot.child("types").getValue();
                        if (allergyTypes != null) {
                            checkBoxFood.setChecked(allergyTypes.get("food"));
                            checkBoxDrug.setChecked(allergyTypes.get("drug"));
                            checkBoxEnvironmental.setChecked(allergyTypes.get("environmental"));
                            checkBoxInsect.setChecked(allergyTypes.get("insect"));
                            checkBoxOther.setChecked(allergyTypes.get("other"));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditAllergiesActivity.this, "Failed to retrieve allergy data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateAllergy() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            String allergen = editTextAllergen.getText().toString().trim();
            String symptoms = editTextSymptoms.getText().toString().trim();
            String triggers = editTextTriggers.getText().toString().trim();
            String previousReactions = editTextPreviousReactions.getText().toString().trim();
            String treatmentPlan = editTextTreatmentPlan.getText().toString().trim();
            String medications = editTextMedications.getText().toString().trim();
            String severity = spinnerSeverity.getSelectedItem().toString();

            String allergyType = "";
            if (checkBoxFood.isChecked()) allergyType += "Food, ";
            if (checkBoxDrug.isChecked()) allergyType += "Drug, ";
            if (checkBoxEnvironmental.isChecked()) allergyType += "Environmental, ";
            if (checkBoxInsect.isChecked()) allergyType += "Insect, ";
            if (checkBoxOther.isChecked()) allergyType += "Other";

            Map<String, Object> allergyMap = new HashMap<>();
            allergyMap.put("allergen", allergen);
            allergyMap.put("symptoms", symptoms);
            allergyMap.put("triggers", triggers);
            allergyMap.put("previousReactions", previousReactions);
            allergyMap.put("treatmentPlan", treatmentPlan);
            allergyMap.put("medications", medications);
            allergyMap.put("severity", severity);

            Map<String, Boolean> allergyTypes = new HashMap<>();
            allergyTypes.put("food", allergyType.contains("Food"));
            allergyTypes.put("drug", allergyType.contains("Drug"));
            allergyTypes.put("environmental", allergyType.contains("Environmental"));
            allergyTypes.put("insect", allergyType.contains("Insect"));
            allergyTypes.put("other", allergyType.contains("Other"));

            allergyMap.put("types", allergyTypes);

            // Update the existing allergy node with the new data
            mDatabase.child("users").child("allergies").child(userId).child(allergyId).updateChildren(allergyMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditAllergiesActivity.this, "Allergy updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditAllergiesActivity.this, "Failed to update allergy", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditAllergiesActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to get the index of an item in a spinner
    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}
