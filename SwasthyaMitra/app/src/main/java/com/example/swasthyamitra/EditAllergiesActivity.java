package com.example.swasthyamitra;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
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

public class EditAllergiesActivity extends AppCompatActivity {

    private TextView textViewAllergen, textViewSymptoms, textViewTriggers, textViewPreviousReactions, textViewTreatmentPlan, textViewMedications;
    private Spinner spinnerSeverity;
    private CheckBox checkBoxFood, checkBoxDrug, checkBoxEnvironmental, checkBoxInsect;

    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_allergies);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Allergies");
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        textViewAllergen = findViewById(R.id.textViewAllergen);
        textViewSymptoms = findViewById(R.id.textViewSymptoms);
        textViewTriggers = findViewById(R.id.textViewTriggers);
        textViewPreviousReactions = findViewById(R.id.textViewPreviousReactions);
        textViewTreatmentPlan = findViewById(R.id.textViewTreatmentPlan);
        textViewMedications = findViewById(R.id.textViewMedications);
        spinnerSeverity = findViewById(R.id.EdspinnerSeverity);
        checkBoxFood = findViewById(R.id.EdcheckBoxFood);
        checkBoxDrug = findViewById(R.id.EdcheckBoxDrug);
        checkBoxEnvironmental = findViewById(R.id.EdcheckBoxEnvironmental);
        checkBoxInsect = findViewById(R.id.EdcheckBoxInsect);

        // Retrieve data from Firebase
        retrieveData();
    }

    private void retrieveData() {
        databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Allergy allergy = dataSnapshot.getValue(Allergy.class);
                    if (allergy != null) {
                        // Set retrieved data to views
                        textViewAllergen.setText(allergy.getAllergen());
                        textViewSymptoms.setText(allergy.getSymptoms());
                        textViewTriggers.setText(allergy.getTriggers());
                        textViewPreviousReactions.setText(allergy.getPreviousReactions());
                        textViewTreatmentPlan.setText(allergy.getTreatmentPlan());
                        textViewMedications.setText(allergy.getMedications());

                        // Set spinner selection
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditAllergiesActivity.this, R.array.spinnerSeverity, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSeverity.setAdapter(adapter);
                        if (allergy.getSeverity() != null) {
                            int position = adapter.getPosition(allergy.getSeverity());
                            spinnerSeverity.setSelection(position);
                        }

                        // Set checkbox state
                        String allergyType = allergy.getAllergyType();
                        if (allergyType != null) {
                            if (allergyType.contains("Food")) {
                                checkBoxFood.setChecked(true);
                            }
                            if (allergyType.contains("Drug")) {
                                checkBoxDrug.setChecked(true);
                            }
                            if (allergyType.contains("Environmental")) {
                                checkBoxEnvironmental.setChecked(true);
                            }
                            if (allergyType.contains("Insect")) {
                                checkBoxInsect.setChecked(true);
                            }
                        }
                    }
                } else {
                    Toast.makeText(EditAllergiesActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditAllergiesActivity.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
