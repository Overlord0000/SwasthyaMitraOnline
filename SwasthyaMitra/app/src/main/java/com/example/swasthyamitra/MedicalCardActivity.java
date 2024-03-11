package com.example.swasthyamitra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MedicalCardActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private TextView fullNameTextView, emergencyContactTextView, addressTextView, genderTextView, bloodGroupTextView;
    private Button generateAndExportButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, medicalHistoryRef; // Separate references for user profile and medical history

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_card);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        // Separate references for better organization
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);//.child("user_profile").child(currentUserId);
        medicalHistoryRef = FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(currentUserId).child("medical_history");

        qrImageView = findViewById(R.id.QR);
        fullNameTextView = findViewById(R.id.FullName);
        emergencyContactTextView = findViewById(R.id.EmergencyContact);
        addressTextView = findViewById(R.id.Adddress);
        genderTextView = findViewById(R.id.Gender);
        bloodGroupTextView = findViewById(R.id.BloodGroup);
        generateAndExportButton = findViewById(R.id.generateAndExportButton);

        generateAndExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserProfileAndMedicalHistory();
            }
        });
    }

    private void fetchUserProfileAndMedicalHistory() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    // Extract user profile data
                    String name = (String) userSnapshot.child("name").getValue();
                    String emergencyContact = (String) userSnapshot.child("emergencyContact").getValue();
                    String address = (String) userSnapshot.child("address").getValue();
                    String gender = (String) userSnapshot.child("gender").getValue();
                    String bloodGroup = (String) userSnapshot.child("bloodGroup").getValue();

                    // Set user profile data to TextViews
                    fullNameTextView.setText("Name: " + name);
                    emergencyContactTextView.setText("Emergency Contact: " + emergencyContact);
                    addressTextView.setText("Address: " + address);
                    genderTextView.setText("Gender: " + gender);
                    bloodGroupTextView.setText("Blood Group: " + bloodGroup);

                    medicalHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot medicalHistorySnapshot) {
                            if (medicalHistorySnapshot.exists()) {
                                // Extract medical history data
                                String existingConditions = (String) medicalHistorySnapshot.child("existingConditions").getValue();
                                String hospitalizationDetails = (String) medicalHistorySnapshot.child("hospitalization").getValue();
                                String allergies = (String) medicalHistorySnapshot.child("allergies").getValue();
                                String alcoholConsumption = (String) medicalHistorySnapshot.child("alcoholConsumption").getValue();
                                String Medications = (String) medicalHistorySnapshot.child("medications").getValue();
                                String restrictions = (String) medicalHistorySnapshot.child("restrictions").getValue();
                                String smoking = (String) medicalHistorySnapshot.child("smoking").getValue();
                                String dailyDiet = (String) medicalHistorySnapshot.child("dailyDiet").getValue();
                                String lastCheckup = (String) medicalHistorySnapshot.child("lastCheckup").getValue();
                                String physicalActivity = (String) medicalHistorySnapshot.child("physicalActivity").getValue();

                                // Create a string representation of medical history data (consider data formatting)
                                StringBuilder medicalHistoryString = new StringBuilder();
// Append user profile details and medical history data to the StringBuilder
                                medicalHistoryString.append("Name: ").append(name).append("\n");
                                medicalHistoryString.append("Emergency Contact: ").append(emergencyContact).append("\n");
// ... append any other relevant user profile details
                                medicalHistoryString.append("Address: ").append(address).append("\n");
                                medicalHistoryString.append("Gender: ").append(gender).append("\n");
                                medicalHistoryString.append("Blood Group: ").append(bloodGroup).append("\n");
                                medicalHistoryString.append("Existing Conditions: ").append(existingConditions).append("\n");
                                medicalHistoryString.append("Hospitalization Details: ").append(hospitalizationDetails).append("\n");
                                medicalHistoryString.append("Allergies: ").append(allergies).append("\n");
                                medicalHistoryString.append("alcoholConsumption: ").append(alcoholConsumption).append("\n");
                                medicalHistoryString.append("Medications: ").append(Medications ).append("\n");
                                medicalHistoryString.append("restrictions: ").append(restrictions ).append("\n");
                                medicalHistoryString.append("smoking: ").append(smoking ).append("\n");
                                medicalHistoryString.append("dailyDiet: ").append(dailyDiet ).append("\n");
                                medicalHistoryString.append("lastCheckup: ").append(lastCheckup ).append("\n");
                                medicalHistoryString.append("physicalActivity: ").append(physicalActivity ).append("\n");

// Generate QR code
                                try {
                                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                    Map<EncodeHintType, String> hints = new HashMap<>();
                                    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // Ensure proper character encoding
                                    BitMatrix bitMatrix = qrCodeWriter.encode(medicalHistoryString.toString(), BarcodeFormat.QR_CODE, 500, 500, hints);

                                    // Create Bitmap from BitMatrix
                                    int width = bitMatrix.getWidth();
                                    int height = bitMatrix.getHeight();
                                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                    for (int x = 0; x < width; x++) {
                                        for (int y = 0; y < height; y++) {
                                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                        }
                                    }

                                    // Set QR code to ImageView
                                    qrImageView.setImageBitmap(bmp);

                                    // Optional: Implement QR code export functionality (refer to previous responses)
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MedicalCardActivity.this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle case where medicalHistorySnapshot does not exist
                                Toast.makeText(MedicalCardActivity.this, "Medical history data not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
// Handle database errors gracefully
                            Toast.makeText(MedicalCardActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
// Handle case where userSnapshot does not exist
                    Toast.makeText(MedicalCardActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
// Handle database errors gracefully
                Toast.makeText(MedicalCardActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
