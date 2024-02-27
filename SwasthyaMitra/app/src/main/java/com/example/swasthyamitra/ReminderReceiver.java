package com.example.swasthyamitra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        // Retrieve the appointment title from the intent
        String title = intent.getStringExtra("title");

        // Display a toast message with the appointment title
        Toast.makeText(context, "Reminder: " + title, Toast.LENGTH_SHORT).show();

        // Log a message to indicate that the reminder was triggered
        Log.d("ReminderReceiver", "Reminder for appointment: " + title);
    }
}
