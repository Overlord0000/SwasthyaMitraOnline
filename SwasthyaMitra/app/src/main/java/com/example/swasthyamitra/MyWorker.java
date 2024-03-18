package com.example.swasthyamitra;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();

        // Extract appointment details from input data
        String title = data.getString("appointmentTitle");
        String location = data.getString("appointmentLocation");
        String timeStr = data.getString("appointmentTime");

        // Construct the notification message with the appointment details
        String notificationMessage = "You have an Appointment for " + title + " in " + location + " at " + timeStr;

        // Send notification with appointment details
        sendNotification(notificationMessage);

        // Return successful result
        return Result.success();
    }

    private void sendNotification(String message) {
        // Build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_ID")
                .setContentTitle("SwasthyaMitra")
                .setContentText(message)
                .setSmallIcon(R.drawable.logo_3_mini) // Replace with your icon resource
                .setAutoCancel(true);

        // Obtain the NotificationManager using Context.getSystemService()
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

}
