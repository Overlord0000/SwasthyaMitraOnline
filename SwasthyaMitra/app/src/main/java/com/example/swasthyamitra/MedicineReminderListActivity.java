package com.example.swasthyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MedicineReminderListActivity extends AppCompatActivity {

    private Button btnAddReminder;
    private RecyclerView recyclerViewReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder_list);

        btnAddReminder = findViewById(R.id.btn_add_reminder);
        recyclerViewReminders = findViewById(R.id.recycler_view_reminders);

        // Set up Recycler view
        recyclerViewReminders.setLayoutManager(new LinearLayoutManager(this));
        // Replace with your actual adapter that displays reminder data
        // MyReminderAdapter adapter = new MyReminderAdapter(this, /* your list of reminders */);
        // recyclerViewReminders.setAdapter(adapter);

        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open activity to add a new reminder (replace with your intent)
                Intent intent = new Intent(MedicineReminderListActivity.this, MedicineReminderListActivity.class);
                startActivity(intent);
            }
        });
    }
}
