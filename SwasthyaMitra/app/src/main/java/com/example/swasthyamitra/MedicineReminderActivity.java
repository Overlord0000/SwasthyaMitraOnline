package com.example.swasthyamitra;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MedicineReminderActivity extends AppCompatActivity {

    private EditText etMedicineName, etDosageNumber, etDosageML, etFrequency, etTime;
    private ToggleButton toggleDosageType;
    private Button btnSaveReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);

        etMedicineName = findViewById(R.id.etMedicineName);
        etDosageNumber = findViewById(R.id.etDosageNumber);
        etDosageML = findViewById(R.id.etDosageML);
        etFrequency = findViewById(R.id.etFrequency);
        etTime = findViewById(R.id.etTime);
        toggleDosageType = findViewById(R.id.toggleDosageType);
        btnSaveReminder = findViewById(R.id.btnSaveReminder);

        btnSaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminder();
            }
        });
    }

    private void saveReminder() {
        String medicineName = etMedicineName.getText().toString().trim();
        String dosageNumber = etDosageNumber.getText().toString().trim();
        String dosageML = etDosageML.getText().toString().trim();
        String frequency = etFrequency.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        boolean dosageTypeIsML = toggleDosageType.isChecked();

        // Validate input fields
        if (medicineName.isEmpty() || dosageNumber.isEmpty() || frequency.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save reminder data to the database or SharedPreferences for the current user
        // Here you should implement code to save the reminder data

        // Schedule alarm for the reminder
        scheduleAlarm(medicineName, dosageNumber, dosageML, frequency, time, dosageTypeIsML);

        Toast.makeText(this, "Reminder saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleAlarm(String medicineName, String dosageNumber, String dosageML, String frequency, String time, boolean dosageTypeIsML) {
        // Convert frequency to milliseconds
        long frequencyInMillis = Long.parseLong(frequency) * 60 * 1000; // assuming frequency is in minutes

        // Split time into hours and minutes
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Set up Calendar for the alarm time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // Set up intent to trigger the alarm
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("medicineName", medicineName);
        alarmIntent.putExtra("dosageNumber", dosageNumber);
        alarmIntent.putExtra("dosageML", dosageML);
        alarmIntent.putExtra("dosageTypeIsML", dosageTypeIsML);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequencyInMillis, pendingIntent);
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String medicineName = intent.getStringExtra("medicineName");
            String dosageNumber = intent.getStringExtra("dosageNumber");
            String dosageML = intent.getStringExtra("dosageML");
            boolean dosageTypeIsML = intent.getBooleanExtra("dosageTypeIsML", false);

            // Display pop-up dialog
            showDialog(context, medicineName, dosageNumber, dosageML, dosageTypeIsML);
        }

        private void showDialog(Context context, String medicineName, String dosageNumber, String dosageML, boolean dosageTypeIsML) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.item_reminder);

            TextView tvMedicineName = dialog.findViewById(R.id.tvMedicineName);
            TextView tvDosage = dialog.findViewById(R.id.tvDosage);
            Button btnTaken = dialog.findViewById(R.id.MedicineTaken);
            Button btnNotTaken = dialog.findViewById(R.id.MedicineNotTaken);

            tvMedicineName.setText(medicineName);

            if (dosageTypeIsML) {
                tvDosage.setText("Dosage: " + dosageML + " ml");
            } else {
                tvDosage.setText("Dosage: " + dosageNumber + " pills");
            }

            btnTaken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnNotTaken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Perform any additional action for "I will not take"
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }
}
