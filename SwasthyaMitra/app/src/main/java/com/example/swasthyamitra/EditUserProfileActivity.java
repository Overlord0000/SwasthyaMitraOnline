package com.example.swasthyamitra;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditUserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;

    private ImageView profilePicture;
    private EditText editName, editDOB, editAge, editEmergencyContact, editAddress;
    private Spinner spinnerBloodGroup;
    private RadioGroup radioGroupGender;
    private Button buttonSave;

    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        profilePicture = findViewById(R.id.EdProfilePicture);
        editName = findViewById(R.id.EdEditName);
        editDOB = findViewById(R.id.EdEditDOB);
        editAge = findViewById(R.id.EdEditAge);
        editEmergencyContact = findViewById(R.id.EdEditEmergencyContact);
        editAddress = findViewById(R.id.EdEditAddress);
        radioGroupGender = findViewById(R.id.EdradioGroupGender);
        spinnerBloodGroup = findViewById(R.id.EdspinnerBloodGroup);
        buttonSave = findViewById(R.id.buttonUpdate); // Changed buttonSave to buttonUpdate

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            // Retrieve user data from Realtime Database
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        if (userProfile != null) {
                            populateUserData(userProfile);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditUserProfileActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile(); // Changed method name to updateUserProfile
            }
        });
    }

    private void populateUserData(UserProfile userProfile) {
        editName.setText(userProfile.getName());
        editDOB.setText(userProfile.getDob());
        editAge.setText(userProfile.getAge());
        editEmergencyContact.setText(userProfile.getEmergencyContact());
        editAddress.setText(userProfile.getAddress());
        int bloodGroupPosition = ((ArrayAdapter<String>) spinnerBloodGroup.getAdapter()).getPosition(userProfile.getBloodGroup());
        spinnerBloodGroup.setSelection(bloodGroupPosition);
        if (userProfile.getGender().equals("Male")) {
            radioGroupGender.check(R.id.EdradioButtonMale);
        } else if(userProfile.getGender().equals("Others")) {
            radioGroupGender.check(R.id.EdradioButtonOther);
        }else {
            radioGroupGender.check(R.id.EdradioButtonFemale);
        }
        // Load profile picture using Glide
        Glide.with(this)
                .load(userProfile.getProfilePictureUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);

    }
    private void loadProfilePicture(String userId) {
        StorageReference profilePicRef = mStorageRef.child("profile_pictures/" + userId + ".jpg");
        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the profile picture into ImageView using Glide
            Glide.with(this)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
        });
    }


    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, (dialog, item) -> {
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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    profilePicture.setImageBitmap(imageBitmap);
                    imageUri = getImageUri(imageBitmap);
                    loadImage(imageUri);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    loadImage(imageUri);
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

    private void loadImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editDOB.setText(sdf.format(calendar.getTime()));
            calculateAge(calendar);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateAge(Calendar dobCalendar) {
        Calendar currentDate = Calendar.getInstance();
        int age = currentDate.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (currentDate.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        editAge.setText(String.valueOf(age));
    }

    private void updateUserProfile() {
        String name = editName.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        String age = editAge.getText().toString().trim();
        String emergencyContact = editEmergencyContact.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
        String gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();

        if (TextUtils.isEmpty(name)) {
            editName.setError("Name is required");
            editName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dob)) {
            editDOB.setError("Date of Birth is required");
            editDOB.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(age)) {
            editAge.setError("Age is required");
            editAge.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(emergencyContact)) {
            editEmergencyContact.setError("Emergency Contact is required");
            editEmergencyContact.requestFocus();
            return;
        }

        if (emergencyContact.length() != 10 || !TextUtils.isDigitsOnly(emergencyContact)) {
            editEmergencyContact.setError("Emergency Contact must be a 10-digit number");
            editEmergencyContact.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            editAddress.setError("Address is required");
            editAddress.requestFocus();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            StorageReference profilePicRef = mStorageRef.child("profile_pictures/" + userId + ".jpg");
            profilePicRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String profilePicUrl = uri.toString();
                            UserProfile userProfile = new UserProfile(name, dob, age, emergencyContact, address, bloodGroup, gender, profilePicUrl);

                            mDatabase.child("users").child(userId).setValue(userProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(EditUserProfileActivity.this, "User profile saved successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditUserProfileActivity.this, "Failed to save user profile", Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditUserProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(EditUserProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
