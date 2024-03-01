package com.example.swasthyamitra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Play sound when the alarm goes off
        playAlarmSound(context);

        // You can also perform other actions here, such as showing a notification
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show();
    }

    private void playAlarmSound(Context context) {
        try {
            // You can change the alarm sound URI here
            Uri alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.medication_reminder);

            // Create a MediaPlayer instance to play the alarm sound
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, alarmSound);
            mediaPlayer.setLooping(true); // Loop the sound continuously until stopped
            mediaPlayer.prepare(); // Prepare the MediaPlayer
            mediaPlayer.start(); // Start playing the sound
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
