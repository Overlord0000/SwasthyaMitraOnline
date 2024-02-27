package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (mAuth.getCurrentUser() == null) {
            // User not logged in, navigate to LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Views
        ImageButton healthProfileBtn = findViewById(R.id.HealthProfilebtn);
        ImageButton medicineManageBtn = findViewById(R.id.MedicicineManagebtn);
        ImageButton appointmentsBtn = findViewById(R.id.Appointmentsbtn);
        ImageButton emergencyToolsBtn = findViewById(R.id.EmergencyToolsbtn);
        ImageButton wellnessBtn = findViewById(R.id.Wellnessbtn);
        ImageButton logoutBtn = findViewById(R.id.btnLogout);

        // Set click listeners for buttons
        healthProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfileActivity.class);
                startActivity(intent);
            }
        });

        medicineManageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedicationManagementActivity.class);
                startActivity(intent);
            }
        });

        appointmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DoctorsAndAppointmentsActivity.class);
                startActivity(intent);
            }
        });

        emergencyToolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyToolsActivity.class);
                startActivity(intent);
            }
        });

        wellnessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WellnessActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user
                mAuth.signOut();
                // Navigate back to LoginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
