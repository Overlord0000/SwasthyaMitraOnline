package com.example.swasthyamitra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "appointment_reminder_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int VIBRATE_PERMISSION_REQUEST_CODE = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        int hoursBefore = intent.getIntExtra("hoursBefore", 0);
        long appointmentTime = intent.getLongExtra("appointmentTime", 0);

        long currentTime = System.currentTimeMillis();
        long remainingTimeMillis = appointmentTime - currentTime;
        long remainingTimeHours = TimeUnit.MILLISECONDS.toHours(remainingTimeMillis);

        showNotification(context, title, location, remainingTimeHours, hoursBefore);
    }

    private void showNotification(Context context, String title, String location, long remainingTimeHours, int hoursBefore) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            createNotification(context, title, location, remainingTimeHours, hoursBefore);
        } else {
            // Request the VIBRATE permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.VIBRATE}, VIBRATE_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void createNotification(Context context, String title, String location, long remainingTimeHours, int hoursBefore) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        createNotificationChannel(context);

        String contentText = String.format("%s at %s in %d hours", title, location, remainingTimeHours);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.reminder)
                .setContentTitle("Appointment Reminder")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Appointment Reminder Channel";
            String description = "Channel for appointment reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}