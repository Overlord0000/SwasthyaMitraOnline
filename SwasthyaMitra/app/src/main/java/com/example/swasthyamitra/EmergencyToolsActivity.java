package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmergencyToolsActivity extends AppCompatActivity {

    private ImageButton emergencyProceduresButton, pastMedicineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_tools);

        emergencyProceduresButton = findViewById(R.id.Emergencyprocedures);
        pastMedicineButton = findViewById(R.id.medicard);

        emergencyProceduresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmergencyProceduresActivity();
            }
        });

        pastMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPastMedicineActivity();
            }
        });
    }

    private void openEmergencyProceduresActivity() {
        // Replace EmergencyProceduresActivity.class with the actual class name of your emergency procedures activity
        Intent intent = new Intent(this, EmergencyProcedureActivity.class);
        startActivity(intent);
    }

    private void openPastMedicineActivity() {
        // Replace PastMedicineActivity.class with the actual class name of your past medicine activity
        Intent intent = new Intent(this, MedicalCardActivity.class);
        startActivity(intent);
    }
}
