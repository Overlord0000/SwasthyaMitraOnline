package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class DoctorsAndAppointmentsActivity extends AppCompatActivity {

    private ImageButton appointmentsButton;
    private ImageButton doctorsNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_and_appointments);

        appointmentsButton = findViewById(R.id.Appointmentsbtn);
        doctorsNoteButton = findViewById(R.id.DoctorsNotebtn);

        appointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AppointmentsActivity when Appointments button is clicked
                startActivity(new Intent(DoctorsAndAppointmentsActivity.this, AppointmentsActivity.class));
            }
        });

        doctorsNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open DoctorsNoteActivity when Doctors Note button is clicked
                startActivity(new Intent(DoctorsAndAppointmentsActivity.this, DoctorsNoteRecyclerView.class));
            }
        });
    }
}
