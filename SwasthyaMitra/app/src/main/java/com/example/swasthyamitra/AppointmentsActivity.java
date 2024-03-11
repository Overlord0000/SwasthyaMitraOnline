package com.example.swasthyamitra;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AppointmentsActivity extends AppCompatActivity {

    private EditText titleAppointment, viewDate, time, location, hoursBefore;
    private Button buttonSetReminder;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        titleAppointment = findViewById(R.id.TitleAppointment);
        viewDate = findViewById(R.id.ViewDate);
        time = findViewById(R.id.Time);
        location = findViewById(R.id.Location);
        hoursBefore = findViewById(R.id.HoursBefore);
        buttonSetReminder = findViewById(R.id.buttonSetReminder);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initDatePicker();
        initTimePicker();

        viewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        buttonSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAppointment();
            }
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                viewDate.setText(selectedDate);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }

    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                time.setText(selectedTime);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, true);
    }

    private void createAppointment() {
        String title = titleAppointment.getText().toString().trim();
        String date = viewDate.getText().toString().trim();
        String timeStr = time.getText().toString().trim();
        String locationStr = location.getText().toString().trim();
        String hoursBeforeStr = hoursBefore.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(timeStr) || TextUtils.isEmpty(hoursBeforeStr)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Appointment appointment = new Appointment();
        appointment.setTitle(title);
        appointment.setDate(date);
        appointment.setTime(timeStr);
        appointment.setLocation(locationStr);

        Calendar calendar = Calendar.getInstance();
        String[] timeParts = timeStr.split(":");
        calendar.set(Integer.parseInt(date.substring(6)), Integer.parseInt(date.substring(3, 5)) - 1, Integer.parseInt(date.substring(0, 2)), Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
        long reminderTime = calendar.getTimeInMillis() - Integer.parseInt(hoursBeforeStr) * 60 * 60 * 1000;
        appointment.setReminderTime(reminderTime);

        db.collection("appointments").document(userId).set(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AppointmentsActivity.this, "Appointment saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AppointmentsActivity.this, "Failed to save appointment.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
