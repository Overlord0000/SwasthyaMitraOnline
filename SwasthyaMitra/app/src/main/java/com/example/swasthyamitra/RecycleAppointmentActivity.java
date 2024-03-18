package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecycleAppointmentActivity extends AppCompatActivity implements AppointmentAdapter.AppointmentClickListener {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private FirebaseAuth mAuth;
    private DatabaseReference appointmentRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_appointment);

        recyclerView = findViewById(R.id.recyclerview);
        mAuth = FirebaseAuth.getInstance();
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appointmentAdapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            appointmentRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Appointments");
            fetchAppointments();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.addnewAppointmentbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecycleAppointmentActivity.this, AppointmentsActivity.class));
            }
        });
    }

    private void fetchAppointments() {
        appointmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecycleAppointmentActivity.this, "Failed to fetch appointments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAppointmentClick(int position) {
        // Handle item click
    }

    @Override
    public void onAppointmentLongClick(int position) {
        // Handle long click
    }
}
