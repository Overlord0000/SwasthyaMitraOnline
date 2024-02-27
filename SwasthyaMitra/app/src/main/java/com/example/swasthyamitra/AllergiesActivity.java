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

public class AllergiesActivity extends AppCompatActivity {

    private EditText editTextAllergen, editTextSymptoms, editTextTriggers, editTextPreviousReactions, editTextTreatmentPlan, editTextMedications;
    private Spinner spinnerSeverity;
    private CheckBox checkBoxFood, checkBoxDrug, checkBoxEnvironmental, checkBoxInsect;
    private Button buttonSaveAllergy;

    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        databaseReference = FirebaseDatabase.getInstance().getReference("Allergies");
        user = FirebaseAuth.getInstance().getCurrentUser();

        editTextAllergen = findViewById(R.id.editTextAllergen);
        editTextSymptoms = findViewById(R.id.editTextSymptoms);
        editTextTriggers = findViewById(R.id.editTextTriggers);
        editTextPreviousReactions = findViewById(R.id.editTextPreviousReactions);
        editTextTreatmentPlan = findViewById(R.id.editTextTreatmentPlan);
        editTextMedications = findViewById(R.id.editTextMedications);
        spinnerSeverity = findViewById(R.id.spinnerSeverity);
        checkBoxFood = findViewById(R.id.checkBoxFood);
        checkBoxDrug = findViewById(R.id.checkBoxDrug);
        checkBoxEnvironmental = findViewById(R.id.checkBoxEnvironmental);
        checkBoxInsect = findViewById(R.id.checkBoxInsect);
        buttonSaveAllergy = findViewById(R.id.buttonSaveAllergy);

        buttonSaveAllergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAllergy();
            }
        });
    }

    private void saveAllergy() {
        String allergen = editTextAllergen.getText().toString().trim();
        String symptoms = editTextSymptoms.getText().toString().trim();
        String triggers = editTextTriggers.getText().toString().trim();
        String previousReactions = editTextPreviousReactions.getText().toString().trim();
        String treatmentPlan = editTextTreatmentPlan.getText().toString().trim();
        String medications = editTextMedications.getText().toString().trim();
        String severity = spinnerSeverity.getSelectedItem().toString();

        StringBuilder allergyType = new StringBuilder();
        if (checkBoxFood.isChecked()) {
            allergyType.append("Food, ");
        }
        if (checkBoxDrug.isChecked()) {
            allergyType.append("Drug, ");
        }
        if (checkBoxEnvironmental.isChecked()) {
            allergyType.append("Environmental, ");
        }
        if (checkBoxInsect.isChecked()) {
            allergyType.append("Insect, ");
        }

        String allergyTypeString = allergyType.toString().trim();
        if (allergyTypeString.endsWith(", ")) {
            allergyTypeString = allergyTypeString.substring(0, allergyTypeString.length() - 2);
        }

        if (allergen.isEmpty() || symptoms.isEmpty() || triggers.isEmpty() || previousReactions.isEmpty() || treatmentPlan.isEmpty() || medications.isEmpty() || allergyTypeString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        Allergy allergy = new Allergy(id, allergen, allergyTypeString, severity, symptoms, triggers, previousReactions, treatmentPlan, medications);

        if (id != null) {
            databaseReference.child(user.getUid()).child(id).setValue(allergy);
            Toast.makeText(this, "Allergy saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving allergy", Toast.LENGTH_SHORT).show();
        }
    }
}
