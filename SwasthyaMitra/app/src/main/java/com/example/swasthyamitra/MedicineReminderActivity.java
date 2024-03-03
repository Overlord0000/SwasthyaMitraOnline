
package com.example.swasthyamitra;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineReminderActivity extends AppCompatActivity {

    private EditText etMedicineName, etDosageNumber, etDosageML, etFrequency;
    private ToggleButton toggleDosageType;
    private EditText etTime;
    private Button btnSaveReminder;

    private List<String> selectedTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);

        etMedicineName = findViewById(R.id.etMedicineName);
        etDosageNumber = findViewById(R.id.etDosageNumber);
        etDosageML = findViewById(R.id.etDosageML);
        etFrequency = findViewById(R.id.etFrequency);
        toggleDosageType = findViewById(R.id.toggleDosageType);
        etTime = findViewById(R.id.etTime);
        btnSaveReminder = findViewById(R.id.btnSaveReminder);

        selectedTimes = new ArrayList<>();

        toggleDosageType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etDosageNumber.setVisibility(View.GONE);
                etDosageML.setVisibility(View.VISIBLE);
            } else {
                etDosageNumber.setVisibility(View.VISIBLE);
                etDosageML.setVisibility(View.GONE);
            }
        });

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
    }

    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                if (selectedTimes.contains(time)) {
                    Toast.makeText(MedicineReminderActivity.this, "Time already selected. Please choose a different time.", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedTimes.add(time);
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

        if (TextUtils.isEmpty(medicineName) || TextUtils.isEmpty(frequencyText) || selectedTimes.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields and select at least one time", Toast.LENGTH_SHORT).show();
            return;
        }

        int frequency = Integer.parseInt(frequencyText);

        for (String time : selectedTimes) {
            setAlarm(medicineName, frequency, time);
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

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicine_name", medicineName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, frequency, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
        // Set alarm for the parsed time without modifications
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        } catch (ParseException e) {
        // Handle parsing exception appropriately
        Toast.makeText(this, "Error setting alarm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        }
        }
