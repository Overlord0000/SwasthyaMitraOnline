package com.example.swasthyamitra;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class QRCodeAndPDFActivity extends AppCompatActivity {

    private static final String TAG = "QRCodeAndPDFActivity";
    private static final String FILE_NAME = "user_profile.pdf";

    private FirebaseFirestore db;

    private TextView nameTextView;
    private TextView phoneNumberTextView;
    private TextView addressTextView;
    private TextView genderTextView;
    private TextView bloodGroupTextView;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_card);

        db = FirebaseFirestore.getInstance();

        nameTextView = findViewById(R.id.FullName);
        phoneNumberTextView = findViewById(R.id.EmergencyContact);
        addressTextView = findViewById(R.id.Adddress);
        genderTextView = findViewById(R.id.Gender);
        bloodGroupTextView = findViewById(R.id.BloodGroup);
        qrCodeImageView = findViewById(R.id.QR);

        // Load user profile data
        loadUserProfileData();

        // Generate and display QR code from medical history data
        generateQRCodeFromMedicalHistory();
    }

    private void loadUserProfileData() {
        db.collection("UserProfile").document("user_id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String name = document.getString("name");
                                String phoneNumber = document.getString("phone_number");
                                String address = document.getString("address");
                                String gender = document.getString("gender");
                                String bloodGroup = document.getString("blood_group");

                                // Set the retrieved data into TextViews
                                nameTextView.setText(name);
                                phoneNumberTextView.setText(phoneNumber);
                                addressTextView.setText(address);
                                genderTextView.setText(gender);
                                bloodGroupTextView.setText(bloodGroup);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void generateQRCodeFromMedicalHistory() {
        // Assuming you have retrieved medical history data from Firebase Firestore
        String medicalHistoryData = "Sample Medical History Data"; // Replace with your actual data

        // Generate QR code bitmap
        Bitmap qrCodeBitmap = generateQRCodeBitmap(medicalHistoryData);
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        } else {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap generateQRCodeBitmap(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void exportDataToPDF() {
        // Combine data from UserProfile and MedicalHistory
        String combinedData = combineDataFromUserProfileAndMedicalHistory();

        // Create PDF file
        File pdfFile = createPDFFile(combinedData);

        // Share or save PDF file as needed
        if (pdfFile != null) {
            // Share or save the PDF file
        } else {
            Toast.makeText(this, "Failed to create PDF file", Toast.LENGTH_SHORT).show();
        }
    }

    private String combineDataFromUserProfileAndMedicalHistory() {
        // Retrieve data from TextViews
        String name = nameTextView.getText().toString();
        String phoneNumber = phoneNumberTextView.getText().toString();
        String address = addressTextView.getText().toString();
        String gender = genderTextView.getText().toString();
        String bloodGroup = bloodGroupTextView.getText().toString();

        // Combine data as needed
        String combinedData = String.format(Locale.getDefault(), "Name: %s\nPhone Number: %s\nAddress: %s\nGender: %s\nBlood Group: %s",
                name, phoneNumber, address, gender, bloodGroup);

        // Assuming you have retrieved medical history data from Firebase Firestore
        String medicalHistoryData = "Sample Medical History Data"; // Replace with your actual data

        // Append medical history data
        combinedData += "\nMedical History:\n" + medicalHistoryData;

        return combinedData;
    }

    private File createPDFFile(String data) {
        // Code to create a PDF file from the combined data and return the File object
        // This is a placeholder method, you'll need to implement the logic to create the PDF file
        return null;
    }
}
