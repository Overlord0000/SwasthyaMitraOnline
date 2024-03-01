package com.example.swasthyamitra;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class AlarmService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        // We don't need to bind to this service, so we return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This method is called when the service is started

        // Retrieve any data passed to the service through the intent
        String medicineName = intent.getStringExtra("medicine_name");

        // Create a MediaPlayer instance and load the sound file
        mediaPlayer = MediaPlayer.create(this, R.raw.medication_reminder); // Replace "your_sound_file" with the name of your sound file in the raw folder

        // Set a completion listener to release the MediaPlayer when the sound finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopSelf(); // Stop the service when the sound finishes playing
            }
        });

        // Start playing the sound
        mediaPlayer.start();

        // Return START_NOT_STICKY to ensure that the service stops itself after handling the intent
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources when the service is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
