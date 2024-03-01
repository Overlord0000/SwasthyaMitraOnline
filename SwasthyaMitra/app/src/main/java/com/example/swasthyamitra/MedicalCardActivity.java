package com.example.swasthyamitra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class MedicalCardActivity extends AppCompatActivity {

    private static final String TAG = "MedicalCardActivity";

    private Button generateButton; // Button text updated to "Generate"
    private ImageView qrImageView;
    private TextView fullNameTextView, emergencyContactTextView, addressTextView, genderTextView, bloodGroupTextView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference medicalHistoryRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String userId; // Replace with your current user identification method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_card);


        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Handle the case where the user is not signed in
            // For example, redirect the user to the login screen
            Toast.makeText(this, "unable to get data", Toast.LENGTH_SHORT).show();

        }


        // Initialize views
        generateButton = findViewById(R.id.generateAndExportButton); // Button reference updated
        qrImageView = findViewById(R.id.QR);
        fullNameTextView = findViewById(R.id.FullName);
        emergencyContactTextView = findViewById(R.id.EmergencyContact);
        addressTextView = findViewById(R.id.Adddress);
        genderTextView = findViewById(R.id.Gender);
        bloodGroupTextView = findViewById(R.id.BloodGroup);

        // Get user ID (replace with your implementation)

        // Get reference to Firebase Database node (child path updated to "MedicalHistory" for accuracy)
        medicalHistoryRef = firebaseDatabase.getReference().child("MedicalHistory").child(userId).child("medical_history");

        // Fetch medical history data and generate QR code on button click
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMedicalHistoryAndGenerateQRCode(); // Method name updated for clarity
            }
        });
    }

    private void fetchMedicalHistoryAndGenerateQRCode() {
        medicalHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Extract relevant medical history data (consider customizing based on your data structure)
                    String existingConditions = (String) snapshot.child("existingConditions").getValue();
                    String hospitalizationDetails = (String) snapshot.child("hospitalizationDetails").getValue();
                    String allergies = (String) snapshot.child("allergies").getValue();
                    String dailyDiet = (String) snapshot.child("dailyDiet").getValue();
                    String lastcheckup = (String) snapshot.child("lastCheckup").getValue();
                    String physcicalActivity = (String) snapshot.child("physicalActivity").getValue();
                    String recentTest = (String) snapshot.child("recentTests").getValue();
                    String restrictions = (String) snapshot.child("recentTests").getValue();
                    String smoking = (String) snapshot.child("smoking").getValue();


                    // Create a string representation of medical history data (consider data formatting)
                    StringBuilder medicalHistoryString = new StringBuilder();
                    medicalHistoryString.append("Existing Conditions: ").append(existingConditions).append("\n");
                    medicalHistoryString.append("Hospitalization Details: ").append(hospitalizationDetails).append("\n");
                    medicalHistoryString.append("allergies Details: ").append(allergies).append("\n");
                    medicalHistoryString.append("daily Details: ").append(dailyDiet).append("\n");
                    medicalHistoryString.append("physicalActivity Details: ").append(physcicalActivity).append("\n");
                    medicalHistoryString.append("restrictions Details: ").append(restrictions).append("\n");
                    medicalHistoryString.append("lastCheckup Details: ").append(lastcheckup).append("\n");
                    medicalHistoryString.append("recentTest Details: ").append(recentTest).append("\n");
                    medicalHistoryString.append("smoking Details: ").append(smoking).append("\n");


                    // Generate QR code
                    try {
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        Map<EncodeHintType, String> hints = new HashMap<>();
                        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                        BitMatrix bitMatrix = qrCodeWriter.encode(medicalHistoryString.toString(), BarcodeFormat.QR_CODE, 500, 500, hints);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        qrImageView.setImageBitmap(bmp);
                    } catch (WriterException e) {
                        e.printStackTrace();
                        Toast.makeText(MedicalCardActivity.this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "No medical history data found");
                    Toast.makeText(MedicalCardActivity.this, "No medical history data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read medical history data", error.toException());
                Toast.makeText(MedicalCardActivity.this, "Failed to read medical history data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
