package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AddAppointmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MaterialButton addNewAppointmentBtn;
    private List<Appointment> appointmentList;
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerview);
        addNewAppointmentBtn = findViewById(R.id.addnewAppointmentbtn);

        // Initialize appointment list
        appointmentList = new ArrayList<>();

        // Initialize adapter
        appointmentAdapter = new AppointmentAdapter(appointmentList);

        // Set layout manager for recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set adapter for recycler view
     //   recyclerView.setAdapter(appointmentAdapter);

        // Set click listener for add new appointment button
        addNewAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open new activity to set a new appointment reminder
                startActivity(new Intent(AddAppointmentActivity.this, AppointmentsActivity.class));
            }
        });
    }
}
