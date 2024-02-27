package com.example.swasthyamitra;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AppointmentsActivity extends AppCompatActivity {

    private EditText appointmentTitleEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private EditText locationEditText;
    private EditText minutesBeforeEditText;
    private Button setReminderButton;

    private DatabaseReference appointmentsRef;

    private static final String CHANNEL_ID = "Appointment_Reminder_Channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        appointmentTitleEditText = findViewById(R.id.textViewTitleAppointment);
        dateEditText = findViewById(R.id.textViewDate);
        timeEditText = findViewById(R.id.textViewTime);
        locationEditText = findViewById(R.id.textViewLocation);
        minutesBeforeEditText = findViewById(R.id.editTextMinutesBefore);
        setReminderButton = findViewById(R.id.buttonSetReminder);

        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("Appointments");

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentAndSetReminder();
            }
        });
    }

    private void saveAppointmentAndSetReminder() {
        String appointmentTitle = appointmentTitleEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String minutesBeforeString = minutesBeforeEditText.getText().toString();

        if (appointmentTitle.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || minutesBeforeString.isEmpty()) {
            // Validate input fields
            // You can display a toast message or handle the empty fields as per your requirement
            return;
        }

        int minutesBefore = Integer.parseInt(minutesBeforeString);

        // Save appointment data to Firebase
        Appointment appointment = new Appointment(appointmentTitle, date, time, location, minutesBefore);
        appointmentsRef.push().setValue(appointment);

        // Set appointment reminder
        setReminder(appointmentTitle, date, time, location, minutesBefore);
    }

    private void setReminder(String appointmentTitle, String date, String time, String location, int minutesBefore) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, -minutesBefore);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", appointmentTitle);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("location", location);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        // Display notification
        showNotification(appointmentTitle, date, time, location);
    }

    private void showNotification(String appointmentTitle, String date, String time, String location) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Appointment Reminder Channel";
            String description = "Channel for appointment reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.reminder)
                .setContentTitle("Appointment Reminder")
                .setContentText("Your appointment \"" + appointmentTitle + "\" is scheduled on " + date + " at " + time + " in " + location)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }
}
