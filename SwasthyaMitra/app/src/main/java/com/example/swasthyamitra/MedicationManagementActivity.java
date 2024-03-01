package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MedicationManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_management);

        // Initialize ImageButtons
        ImageButton prescriptionsBtn = findViewById(R.id.Prescriptionsbtn);
        ImageButton medicineRemindersBtn = findViewById(R.id.MedicineRemindersbtn);
        ImageButton medicineInventoryTrackerBtn = findViewById(R.id.MedicineInventoryTrackerbtn);
        ImageButton stockAlertBtn = findViewById(R.id.StockAlertbtn);

        // Set onClickListeners for ImageButtons
        prescriptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrescriptionActivity.class);
                startActivity(intent);
            }
        });

        medicineRemindersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedicineReminderActivity.class);
                startActivity(intent);
            }
        });

        medicineInventoryTrackerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedicineInventoryTrackerActivity.class);
                startActivity(intent);
            }
        });

        stockAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StockAlertActivity.class);
                startActivity(intent);
            }
        });
    }
}
