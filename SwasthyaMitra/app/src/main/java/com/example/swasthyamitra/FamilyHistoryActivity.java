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

public class FamilyHistoryActivity extends AppCompatActivity {

    private EditText chronicIllnessesEditText, hereditaryConditionsEditText, heartAttacksStrokesEditText,
            highCholesterolBloodPressureEditText, cancerHistoryEditText, geneticTestingEditText,
            neurologicalConditionsEditText;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference muserFamilyHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_history);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            muserFamilyHistoryRef = mDatabase.getReference().child("FamilyMedicalHistory").child(userId).child("Family_History");
        }


        chronicIllnessesEditText = findViewById(R.id.ChronicIllnesses);
        hereditaryConditionsEditText = findViewById(R.id.HereditaryConditions);
        heartAttacksStrokesEditText = findViewById(R.id.HeartAttacksStrokes);
        highCholesterolBloodPressureEditText = findViewById(R.id.HighCholesterolBloodPressure);
        cancerHistoryEditText = findViewById(R.id.CancerHistory);
        geneticTestingEditText = findViewById(R.id.GeneticTesting);
        neurologicalConditionsEditText = findViewById(R.id.NeurologicalConditions);

        saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        String chronicIllnesses = chronicIllnessesEditText.getText().toString().trim();
        String hereditaryConditions = hereditaryConditionsEditText.getText().toString().trim();
        String heartAttacksStrokes = heartAttacksStrokesEditText.getText().toString().trim();
        String highCholesterolBloodPressure = highCholesterolBloodPressureEditText.getText().toString().trim();
        String cancerHistory = cancerHistoryEditText.getText().toString().trim();
        String geneticTesting = geneticTestingEditText.getText().toString().trim();
        String neurologicalConditions = neurologicalConditionsEditText.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FamilyHistory familyHistory = new FamilyHistory(chronicIllnesses, hereditaryConditions,
                    heartAttacksStrokes, highCholesterolBloodPressure, cancerHistory, geneticTesting,
                    neurologicalConditions);

            muserFamilyHistoryRef.child(userId).setValue(familyHistory);

            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

}
