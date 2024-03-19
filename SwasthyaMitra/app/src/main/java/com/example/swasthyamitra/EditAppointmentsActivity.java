package com.example.swasthyamitra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class EditAppointmentsActivity extends AppCompatActivity {

    private EditText titleEditText, dateEditText, timeEditText, locationEditText, hoursBeforeEditText;
    private Button updateReminderButton;
    private String appointmentId;
    private DatabaseReference appointmentRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointments);

        titleEditText = findViewById(R.id.TitleAppointmentEd);
        dateEditText = findViewById(R.id.ViewDateEd);
        timeEditText = findViewById(R.id.TimeEd);
        locationEditText = findViewById(R.id.LocationEd);
        hoursBeforeEditText = findViewById(R.id.HoursBeforeEd);
        updateReminderButton = findViewById(R.id.buttonUpdateReminder);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            appointmentRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Appointments");
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Retrieve appointment details from intent
        appointmentId = getIntent().getStringExtra("APPOINTMENT_ID");
        if (appointmentId != null) {
            retrieveAppointmentDetails();
        } else {
            Toast.makeText(this, "No appointment selected", Toast.LENGTH_SHORT).show();
            finish();
        }

        updateReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppointmentAndReminder();
            }
        });
    }

    private void retrieveAppointmentDetails() {
        appointmentRef.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Appointment appointment = dataSnapshot.getValue(Appointment.class);
                if (appointment != null) {
                    // Set retrieved appointment details to EditText fields
                    titleEditText.setText(appointment.getTitle());
                    dateEditText.setText(String.valueOf(appointment.getDateInMillis()));
                    timeEditText.setText(String.valueOf(appointment.getTimeInMillis()));
                    locationEditText.setText(appointment.getLocation());
                    hoursBeforeEditText.setText(String.valueOf(appointment.getHoursBefore()));
                } else {
                    Toast.makeText(EditAppointmentsActivity.this, "Appointment details not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditAppointmentsActivity.this, "Failed to retrieve appointment details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateAppointmentAndReminder() {
        // Validate input fields
        if (!validateInput()) {
            return;
        }

        // Get updated appointment details
        String title = titleEditText.getText().toString();
        String dateStr = dateEditText.getText().toString();
        String timeStr = timeEditText.getText().toString();
        String location = locationEditText.getText().toString();
        int hoursBefore = Integer.parseInt(hoursBeforeEditText.getText().toString());

        // Update appointment in Firebase
        Appointment updatedAppointment = new Appointment(appointmentId, title, Long.parseLong(dateStr), Long.parseLong(timeStr), location, hoursBefore);
        appointmentRef.child(appointmentId).setValue(updatedAppointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditAppointmentsActivity.this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                        // Reschedule reminder
                        rescheduleReminder(updatedAppointment);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditAppointmentsActivity.this, "Failed to update appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void rescheduleReminder(Appointment updatedAppointment) {
        // Calculate new notification time based on updated appointment details
        long notificationTimeInMillis = updatedAppointment.getDateInMillis() + updatedAppointment.getTimeInMillis() - (updatedAppointment.getHoursBefore() * 60 * 60 * 1000);

        // Create a Data object containing updated appointment details
        Data inputData = new Data.Builder()
                .putString("title", updatedAppointment.getTitle())
                .putLong("dateInMillis", updatedAppointment.getDateInMillis())
                .putString("location", updatedAppointment.getLocation())
                .putLong("time", updatedAppointment.getTimeInMillis())
                // Add other appointment details as needed
                .build();

        // Schedule the notification using WorkManager
        OneTimeWorkRequest notificationWorkRequest =
                new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInitialDelay(notificationTimeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(notificationWorkRequest);
    }


    private boolean validateInput() {
        // Implement input validation logic here
        return true; // Return true if input is valid, false otherwise
    }
}
