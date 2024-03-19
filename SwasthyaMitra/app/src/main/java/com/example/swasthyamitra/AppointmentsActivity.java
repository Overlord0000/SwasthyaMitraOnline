package com.example.swasthyamitra;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppointmentsActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private EditText locationEditText;
    private EditText hoursBeforeEditText;
    private Button setReminderButton;
    private Calendar now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Initialize UI elements
        titleEditText = findViewById(R.id.TitleAppointment);
        dateEditText = findViewById(R.id.ViewDate);
        timeEditText = findViewById(R.id.Time);
        locationEditText = findViewById(R.id.Location);
        hoursBeforeEditText = findViewById(R.id.HoursBefore);
        setReminderButton = findViewById(R.id.buttonSetReminder);

        // Initialize current date/time
        now = Calendar.getInstance();

        // Set date/time picker click listeners
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Set reminder button click listener
        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate user input
                if (validateInput()) {
                    // If input is valid, proceed with saving appointment data and scheduling notification
                    saveAppointmentAndScheduleNotification();
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Validate title
        if (titleEditText.getText().toString().isEmpty()) {
            titleEditText.setError("Please enter a title");
            isValid = false;
        }

        // Validate date (not in the past, today's date or future date)
        String dateStr = dateEditText.getText().toString();
        long selectedDateInMillis = getDateInMillis(dateStr);

        // Get the current date without time
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (selectedDateInMillis < today.getTimeInMillis()) {
            dateEditText.setError("Please enter today's date or a future date");
            isValid = false;
        } else {
            // Validate time (not in the past for the selected date)
            String timeStr = timeEditText.getText().toString();
            long selectedTimeInMillis = getTimeInMillis(timeStr);

            // Combine the selected date and time
            Calendar selectedDateTime = Calendar.getInstance();
            selectedDateTime.setTimeInMillis(selectedDateInMillis);
            selectedDateTime.set(Calendar.HOUR_OF_DAY, getHourFromTime(timeStr));
            selectedDateTime.set(Calendar.MINUTE, getMinuteFromTime(timeStr));

            // Get the current time in milliseconds
            long currentTimeInMillis = System.currentTimeMillis();

            // Check if the selected date and time are at least 1 hour ahead of the current time
            long minimumAllowedTimeInMillis = currentTimeInMillis + (60 * 60 * 1000); // 1 hour in milliseconds
            if (selectedDateTime.getTimeInMillis() < minimumAllowedTimeInMillis) {
                timeEditText.setError("Please enter a date and time at least 1 hour ahead of the current time");
                isValid = false;
            }
        }

        // Validate hours before (maximum 24 hours)
        int hoursBefore = Integer.parseInt(hoursBeforeEditText.getText().toString());
        if (hoursBefore <= 0 || hoursBefore > 24) {
            hoursBeforeEditText.setError("Please enter a value between 1 and 24");
            isValid = false;
        }

        return isValid;
    }

    private int getHourFromTime(String timeStr) {
        String[] parts = timeStr.split(":");
        return Integer.parseInt(parts[0]);
    }

    private int getMinuteFromTime(String timeStr) {
        String[] parts = timeStr.split(":");
        return Integer.parseInt(parts[1]);
    }
    private long getDateInMillis(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long getTimeInMillis(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return sdf.parse(timeStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateEditText.setText(sdf.format(selectedDate.getTime()));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, dateSetListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeEditText.setText(timeStr);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, timeSetListener, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false
        );
        timePickerDialog.show();
    }

// Inside the class AppointmentsActivity

    private Data inputData;

    private void saveAppointmentAndScheduleNotification() {
        // Validate input fields
        if (!validateInput()) {
            return;
        }

        // Get appointment details from input fields
        String title = titleEditText.getText().toString();
        String dateStr = dateEditText.getText().toString();
        String timeStr = timeEditText.getText().toString();
        String location = locationEditText.getText().toString();
        int hoursBefore = Integer.parseInt(hoursBeforeEditText.getText().toString());

        Context context = getApplicationContext();

        // Obtain the current user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        // Calculate appointment time in milliseconds
        long appointmentTimeInMillis = getDateInMillis(dateStr) + getTimeInMillis(timeStr);

        // Calculate the time for the notification to be triggered
        long notificationTimeInMillis = appointmentTimeInMillis - (hoursBefore * 60 * 60 * 1000);

        // Initialize inputData here
        inputData = new Data.Builder()
                .putString("appointmentTitle", title)
                .putString("appointmentLocation", location)
                .putString("appointmentTime", timeStr)
                .build();

        // Schedule the notification using WorkManager
        long delayInMillis = notificationTimeInMillis - System.currentTimeMillis();
        OneTimeWorkRequest notificationWorkRequest =
                new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();
        WorkManager.getInstance(context).enqueue(notificationWorkRequest);

        // Save appointment details to Firebase
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Appointments");
        String appointmentId = appointmentsRef.push().getKey();
        Appointment appointment = new Appointment(appointmentId, title, getDateInMillis(dateStr), getTimeInMillis(timeStr), location, hoursBefore);
        appointmentsRef.child(appointmentId).setValue(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AppointmentsActivity.this, "Appointment saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AppointmentsActivity.this, "Failed to save appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
