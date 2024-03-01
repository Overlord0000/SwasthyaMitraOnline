package com.example.swasthyamitra;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AppointmentsActivity extends AppCompatActivity {

    private EditText appointmentTitleEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private EditText locationEditText;
    private EditText minutesBeforeEditText;
    private Button setReminderButton;

    private DatabaseReference appointmentsRef;

    private static final String CHANNEL_ID = "Appointment_Reminder_Channel";
    private Calendar calendar;

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

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentAndSetReminder();
            }
        });
    }

    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int mYear = currentDate.get(Calendar.YEAR);
        int mMonth = currentDate.get(Calendar.MONTH);
        int mDay = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        currentDate.set(Calendar.YEAR, year);
                        currentDate.set(Calendar.MONTH, monthOfYear);
                        currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        dateEditText.setText(sdf.format(currentDate.getTime()));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar currentTime = Calendar.getInstance();
        int mHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int mMinute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        currentTime.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        timeEditText.setText(sdf.format(currentTime.getTime()));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
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
