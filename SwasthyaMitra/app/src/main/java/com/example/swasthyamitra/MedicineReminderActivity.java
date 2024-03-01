package com.example.swasthyamitra;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        btnSaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminder();
            }
        });

        toggleDosageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleDosageType.isChecked()) {
                    etDosageNumber.setVisibility(View.GONE);
                    etDosageML.setVisibility(View.VISIBLE);
                } else {
                    etDosageNumber.setVisibility(View.VISIBLE);
                    etDosageML.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                etTime.setText(time);
            }
        };

        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, false);
        timePickerDialog.show();
    }

    private void saveReminder() {
        String medicineName = etMedicineName.getText().toString().trim();
        String frequencyText = etFrequency.getText().toString().trim();
        String timeText = etTime.getText().toString().trim();

        if (medicineName.isEmpty() || frequencyText.isEmpty() || timeText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int frequency = Integer.parseInt(frequencyText);
        String[] times = timeText.split(",");

        for (String time : times) {
            setAlarm(medicineName, frequency, time.trim());
        }

        Toast.makeText(this, "Reminder saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setAlarm(String medicineName, int frequency, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date alarmTime = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alarmTime);

            for (int i = 0; i < frequency; i++) {
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("medicine_name", medicineName);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    calendar.add(Calendar.HOUR_OF_DAY, 24 / frequency);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
