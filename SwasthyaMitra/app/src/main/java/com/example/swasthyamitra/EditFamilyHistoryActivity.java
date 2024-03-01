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

public class EditFamilyHistoryActivity extends AppCompatActivity {

    private EditText chronicIllnessesEditText, hereditaryConditionsEditText, heartAttacksStrokesEditText,
            highCholesterolBloodPressureEditText, cancerHistoryEditText, geneticTestingEditText, neurologicalConditionsEditText;
    private Button updateButton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserFamilyHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_family_history);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mUserFamilyHistoryRef = mDatabase.getReference().child("FamilyMedicalHistory").child(userId).child("Family_History");

            mUserFamilyHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        FamilyHistory familyHistory = dataSnapshot.getValue(FamilyHistory.class);
                        if (familyHistory != null) {
                            chronicIllnessesEditText.setText(familyHistory.getChronicIllnesses());
                            hereditaryConditionsEditText.setText(familyHistory.getHereditaryConditions());
                            heartAttacksStrokesEditText.setText(familyHistory.getHeartAttacksStrokes());
                            highCholesterolBloodPressureEditText.setText(familyHistory.getHighCholesterolBloodPressure());
                            cancerHistoryEditText.setText(familyHistory.getCancerHistory());
                            geneticTestingEditText.setText(familyHistory.getGeneticTesting());
                            neurologicalConditionsEditText.setText(familyHistory.getNeurologicalConditions());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditFamilyHistoryActivity.this, "Unable to reach server", Toast.LENGTH_SHORT).show();
                }
            });
        }


        chronicIllnessesEditText = findViewById(R.id.ChronicIllnesses);
        hereditaryConditionsEditText = findViewById(R.id.HereditaryConditions);
        heartAttacksStrokesEditText = findViewById(R.id.HeartAttacksStrokes);
        highCholesterolBloodPressureEditText = findViewById(R.id.HighCholesterolBloodPressure);
        cancerHistoryEditText = findViewById(R.id.CancerHistory);
        geneticTestingEditText = findViewById(R.id.GeneticTesting);
        neurologicalConditionsEditText = findViewById(R.id.EdNeurologicalConditions);
        updateButton = findViewById(R.id.buttonUpdate);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFamilyHistory();
            }
        });
    }

    private void updateFamilyHistory() {
        String chronicIllnesses = chronicIllnessesEditText.getText().toString();
        String hereditaryConditions = hereditaryConditionsEditText.getText().toString();
        String heartAttacksStrokes = heartAttacksStrokesEditText.getText().toString();
        String highCholesterolBloodPressure = highCholesterolBloodPressureEditText.getText().toString();
        String cancerHistory = cancerHistoryEditText.getText().toString();
        String geneticTesting = geneticTestingEditText.getText().toString();
        String neurologicalConditions = neurologicalConditionsEditText.getText().toString();

        FamilyHistory familyHistory = new FamilyHistory(chronicIllnesses,
                hereditaryConditions,
                heartAttacksStrokes,
                highCholesterolBloodPressure,
                cancerHistory,
                geneticTesting,
                neurologicalConditions);

        mUserFamilyHistoryRef.setValue(familyHistory)
                .addOnSuccessListener(aVoid -> Toast.makeText(EditFamilyHistoryActivity.this, "Family history updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(EditFamilyHistoryActivity.this, "Failed to update family history: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
