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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AllergiesActivity extends AppCompatActivity {
    private EditText editTextAllergen, editTextSymptoms, editTextTriggers, editTextPreviousReactions, editTextTreatmentPlan, editTextMedications;
    private CheckBox checkBoxFood, checkBoxDrug, checkBoxEnvironmental, checkBoxInsect, checkBoxOther;
    private Spinner spinnerSeverity;
    private Button buttonSaveAllergy;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextAllergen = findViewById(R.id.editTextAllergen);
        editTextSymptoms = findViewById(R.id.editTextSymptoms);
        editTextTriggers = findViewById(R.id.editTextTriggers);
        editTextPreviousReactions = findViewById(R.id.editTextPreviousReactions);
        editTextTreatmentPlan = findViewById(R.id.editTextTreatmentPlan);
        editTextMedications = findViewById(R.id.editTextMedications);

        checkBoxFood = findViewById(R.id.checkBoxFood);
        checkBoxDrug = findViewById(R.id.checkBoxDrug);
        checkBoxEnvironmental = findViewById(R.id.checkBoxEnvironmental);
        checkBoxInsect = findViewById(R.id.checkBoxInsect);
        checkBoxOther = findViewById(R.id.checkBoxOther);

        spinnerSeverity = findViewById(R.id.spinnerSeverity);

        buttonSaveAllergy = findViewById(R.id.buttonSaveAllergy);
        buttonSaveAllergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAllergy();
            }
        });
    }

    private void saveAllergy() {
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

            Map<String, Object> allergyMap = new HashMap<>();
            allergyMap.put("allergen", allergen);
            allergyMap.put("symptoms", symptoms);
            allergyMap.put("triggers", triggers);
            allergyMap.put("previousReactions", previousReactions);
            allergyMap.put("treatmentPlan", treatmentPlan);
            allergyMap.put("medications", medications);
            allergyMap.put("severity", severity);

            // Set allergy types
            Map<String, Boolean> allergyTypes = new HashMap<>();
            allergyTypes.put("food", checkBoxFood.isChecked());
            allergyTypes.put("drug", checkBoxDrug.isChecked());
            allergyTypes.put("environmental", checkBoxEnvironmental.isChecked());
            allergyTypes.put("insect", checkBoxInsect.isChecked());
            allergyTypes.put("other", checkBoxOther.isChecked());

            allergyMap.put("types", allergyTypes);

            mDatabase.child("users").child("allergies").child(userId).push().setValue(allergyMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AllergiesActivity.this, "Allergy saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AllergiesActivity.this, "Failed to save allergy", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AllergiesActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
