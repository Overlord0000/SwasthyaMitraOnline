package com.example.swasthyamitra;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_IMAGE_PICK = 103;

    private EditText editName, editAge, editDOB, editEmergencyContact, editAddress;
    private Spinner spinnerBloodGroup;
    private RadioGroup radioGroupGender;
    private Button btnSave;
    private ImageView profilePicture;

    // Firebase Database
    private DatabaseReference databaseRef;

    // Firebase Storage
    private StorageReference storageRef;

    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId =(String) currentUser.getUid();
            databaseRef = mDatabase.getReference().child("UserProfile").child(userId).child("user_profile");
            storageRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures").child(userId).child("User_profile_pic");

        }

        // Initialize Firebase Database reference

        editName = findViewById(R.id.EditName);
        editAge = findViewById(R.id.EditAge);
        editDOB = findViewById(R.id.EditDOB);
        editEmergencyContact = findViewById(R.id.EditEmergencyContact);
        editAddress = findViewById(R.id.EditAddress);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnSave = findViewById(R.id.buttonSave);
        profilePicture = findViewById(R.id.ProfilePicture);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);

        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
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
                        editDOB.setText(sdf.format(currentDate.getTime()));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if (checkCameraPermission()) {
                        openCamera();
                    } else {
                        requestCameraPermission();
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    openGallery();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profilePicture.setImageBitmap(imageBitmap);
                imageUri = getImageUri(imageBitmap);
                Glide.with(this)
                        .load(imageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                if (data != null) {
                    imageUri = data.getData();
                    Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePicture);
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void saveUserProfile() {
        String name = editName.getText().toString().trim();
        String age = editAge.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        String emergencyContact = editEmergencyContact.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString().trim();
        String gender = getSelectedGender();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(dob) ||
                TextUtils.isEmpty(emergencyContact) || TextUtils.isEmpty(address) || TextUtils.isEmpty(bloodGroup) ||
                TextUtils.isEmpty(gender) || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload profile picture to Firebase Storage
        final StorageReference profilePicRef = storageRef.child(name + "_profile_picture.jpg");
        profilePicRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL for the profile picture
                        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Save user profile data to Realtime Database
                                Map<String, Object> userProfileData = new HashMap<>();
                                userProfileData.put("name", name);
                                userProfileData.put("age", age);
                                userProfileData.put("dob", dob);
                                userProfileData.put("emergencyContact", emergencyContact);
                                userProfileData.put("address", address);
                                userProfileData.put("bloodGroup", bloodGroup);
                                userProfileData.put("gender", gender);
                               userProfileData.put("profilePicture", uri.toString());



                                // Save the data to Realtime Database
                                databaseRef.child(userId).setValue(userProfileData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UserProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UserProfileActivity.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, "Failed to upload profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSelectedGender() {
        int selectedRadioButtonId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton radioButton = findViewById(selectedRadioButtonId);
            return radioButton.getText().toString();
        } else {
            return "";
        }
    }
}
