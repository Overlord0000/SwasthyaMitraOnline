package com.example.swasthyamitra;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditUserProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditUserProfileActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_IMAGE_PICK = 102;

    private ImageView profilePicture;
    private EditText editName, editAge, editDOB, editEmergencyContact, editAddress;
    private Spinner spinnerBloodGroup;
    private RadioGroup radioGroupGender;
    private Button btnUpdate;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private ArrayAdapter<CharSequence> adapter;


    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI elements
        profilePicture = findViewById(R.id.EdProfilePicture);
        editName = findViewById(R.id.EdEditName);
        editAge = findViewById(R.id.EdEditAge);
        editDOB = findViewById(R.id.EdEditDOB);
        editEmergencyContact = findViewById(R.id.EdEditEmergencyContact);
        editAddress = findViewById(R.id.EdEditAddress);
        spinnerBloodGroup = findViewById(R.id.EdspinnerBloodGroup);
        radioGroupGender = findViewById(R.id.EdradioGroupGender);
        btnUpdate = findViewById(R.id.buttonUpdate);

        // Check if user is logged in
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(userId).child("user_profile");
            storageRef = FirebaseStorage.getInstance().getReference().child("profile_pictures");

            // Retrieve user profile data
            retrieveUserProfileData();
        } else {
            Toast.makeText(this, "Please Login to access profile", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if user is not logged in
        }

        // Set up blood group spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                // ...
                // Set up blood group spinner (continued)
                R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);

        // Handle profile picture click (open dialog for camera or gallery)
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });

        // Handle update button click
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    private void showImageSelectionDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Request camera permission
                    requestCameraPermission();
                } else if (which == 1) {
                    // Open gallery to choose image
                    openImagePicker();
                }
            }
        });
        builder.create().show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Permission already granted, open camera
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Image captured from camera
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePicture.setImageBitmap(imageBitmap);
            imageUri = null; // Handle captured image in-memory
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            // Image selected from gallery
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }

    private void retrieveUserProfileData() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> userProfileData = (Map<String, Object>) snapshot.getValue();

                    if (userProfileData != null) {
                        String name = (String) userProfileData.get("name");
                        String age = (String) userProfileData.get("age");
                        String dob = (String) userProfileData.get("dob");
                        String emergencyContact = (String) userProfileData.get("emergencyContact");
                        String address = (String) userProfileData.get("address");
                        String bloodGroup = (String) userProfileData.get("bloodGroup");
                        String gender = (String) userProfileData.get("gender");

                        // Set UI element values
                        editName.setText(name);
                        editAge.setText(age);
                        editDOB.setText(dob);
                        editEmergencyContact.setText(emergencyContact);
                        editAddress.setText(address);

                        // Set blood group spinner selection
                        int bloodGroupIndex = adapter.getPosition(bloodGroup);
                        spinnerBloodGroup.setSelection(bloodGroupIndex);

                        // Select gender radio button
                        if (gender.equals("Male")) {
                            ((RadioButton) findViewById(R.id.EdradioButtonMale)).setChecked(true);
                        } else {
                            ((RadioButton) findViewById(R.id.EdradioButtonFemale)).setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditUserProfileActivity.this, "Failed to retrieve user profile data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String name = editName.getText().toString().trim();
        String age = editAge.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        String emergencyContact = editEmergencyContact.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedGenderId);
        String gender = selectedRadioButton.getText().toString();

        // Validate user input (optional)

        // Update profile data in Firebase Database
        Map<String, Object> userProfileData = new HashMap<>();
        userProfileData.put("name", name);
        userProfileData.put("age", age);
        userProfileData.put("dob", dob);
        userProfileData.put("emergencyContact", emergencyContact);
        userProfileData.put("address", address);
        userProfileData.put("bloodGroup", bloodGroup);
        userProfileData.put("gender", gender);

        if (imageUri != null) {
            // Upload profile picture to Firebase Storage
            uploadProfilePicture(userProfileData);
        } else {
            // Update profile data without image change
            databaseRef.updateChildren(userProfileData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditUserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after successful update
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditUserProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadProfilePicture(Map<String, Object> userProfileData) {
        final StorageReference imageRef = storageRef.child(userId + ".jpg");

        if (imageUri != null) {
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        userProfileData.put("profilePicture", downloadUri.toString());

                                        // Update profile data with image URL
                                        databaseRef.updateChildren(userProfileData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(EditUserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                finish(); // Close activity after successful update
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditUserProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }


                                        });
                                    } else {
                                        // Handle failed download URL retrieval
                                        Toast.makeText(EditUserProfileActivity.this, "Failed to get image download URL", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditUserProfileActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
