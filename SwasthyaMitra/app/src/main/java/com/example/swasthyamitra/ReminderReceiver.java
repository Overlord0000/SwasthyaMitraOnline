package com.example.swasthyamitra;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Play sound when the alarm goes off
//        playAlarmSound(context);
//        if (intent.getAction() != null && intent.getAction().equals("MY_ALARM_ACTION ")) {
//            // Start the PopupService to display the popup menu
//            Intent serviceIntent = new Intent(context, PopupService.class);
//            context.startService(serviceIntent);
//        }
//
//        // You can also perform other actions here, such as showing a notification
//        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");

        // Perform actions when the reminder is received
        // For example, display a notification, show a dialog, etc.
        // In this example, we will display a toast message
        String reminderMessage = "Don't forget your appointment \"" + title + "\" scheduled on " + date + " at " + time + " in " + location;
        Toast.makeText(context, reminderMessage, Toast.LENGTH_LONG).show();
    }

//    private void playAlarmSound(Context context) {
//        try {
//            // You can change the alarm sound URI here
//            Uri alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.medication_reminder);
//
//            // Create a MediaPlayer instance to play the alarm sound
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            mediaPlayer.setDataSource(context, alarmSound);
//            mediaPlayer.setLooping(true); // Loop the sound continuously until stopped
//            mediaPlayer.prepare(); // Prepare the MediaPlayer
//            mediaPlayer.start(); // Start playing the sound
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}