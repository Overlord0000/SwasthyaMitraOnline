package com.example.swasthyamitra;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CurrentMedicineActivity extends AppCompatActivity {

    EditText editTextMedicationName, editTextDosage, editTextFrequency, editTextSchedule, editTextStartDate, editTextEndDate, editTextNotes;
    Button buttonSave;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_medicine);

        editTextMedicationName = findViewById(R.id.editTextMedicationName);
        editTextDosage = findViewById(R.id.editTextDosage);
        editTextFrequency = findViewById(R.id.editTextFrequency);
        editTextSchedule = findViewById(R.id.editTextSchedule);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextNotes = findViewById(R.id.editTextNotes);
        buttonSave = findViewById(R.id.buttonSave);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("CurrentMedications");

        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(editTextStartDate);
            }
        });

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(editTextEndDate);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicine();
            }
        });
    }

    private void showDatePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                editText.setText(dateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveMedicine() {
        String medicationName = editTextMedicationName.getText().toString().trim();
        String dosageStr = editTextDosage.getText().toString().trim();
        String frequencyStr = editTextFrequency.getText().toString().trim();
        String schedule = editTextSchedule.getText().toString().trim();
        String startDateString = editTextStartDate.getText().toString().trim();
        String endDateString = editTextEndDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (TextUtils.isEmpty(medicationName)) {
            editTextMedicationName.setError("Enter medication name");
            return;
        }

        if (TextUtils.isEmpty(dosageStr)) {
            editTextDosage.setError("Enter dosage");
            return;
        }

        if (TextUtils.isEmpty(frequencyStr)) {
            editTextFrequency.setError("Enter frequency");
            return;
        }

        if (TextUtils.isEmpty(schedule)) {
            editTextSchedule.setError("Enter schedule");
            return;
        }

        if (TextUtils.isEmpty(startDateString)) {
            editTextStartDate.setError("Select start date");
            return;
        }

        if (TextUtils.isEmpty(endDateString)) {
            editTextEndDate.setError("Select end date");
            return;
        }

        if (TextUtils.isEmpty(notes)) {
            editTextNotes.setError("Enter notes");
            return;
        }

        int dosage = Integer.parseInt(dosageStr);
        int frequency = Integer.parseInt(frequencyStr);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date startDate, endDate;
        try {
            startDate = dateFormat.parse(startDateString);
            endDate = dateFormat.parse(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing dates", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        if (id != null) {
            CurrentMedModel currentMedModel = new CurrentMedModel(id, medicationName, dosage, frequency, schedule, startDate, endDate, notes);
            databaseReference.child(id).setValue(currentMedModel);
            Toast.makeText(this, "Medicine added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add medicine", Toast.LENGTH_SHORT).show();
        }
    }

}
