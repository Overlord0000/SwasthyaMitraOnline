package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PrescriptionActivity extends AppCompatActivity {

    private ImageButton currentMedicineBtn, pastMedicineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        // Initialize views
        currentMedicineBtn = findViewById(R.id.CurrentMedicinebtn);
        pastMedicineBtn = findViewById(R.id.PastMedicinebtn);

        // Set click listeners
        currentMedicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open CurrentMedicineActivity
                startActivity(new Intent(PrescriptionActivity.this, CurrentMedicineActivity.class));
            }
        });

        pastMedicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open PastMedicineActivity
                startActivity(new Intent(PrescriptionActivity.this, PastMedineActivity.class));
            }
        });
    }
}
