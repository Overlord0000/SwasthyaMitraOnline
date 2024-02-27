package com.example.swasthyamitra;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editName, editAge, editDOB, editEmergencyContact, editAddress;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale, radioButtonFemale;
    private Spinner spinnerBloodGroup;
    private Button buttonSave;
    private ImageView profilePicture;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    private Uri mImageUri;
    private String mImageUrl;

    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        editName = findViewById(R.id.EditName);
        editAge = findViewById(R.id.EditAge);
        editDOB = findViewById(R.id.EditDOB);
        editEmergencyContact = findViewById(R.id.EditEmergencyContact);
        editAddress = findViewById(R.id.EditAddress);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        buttonSave = findViewById(R.id.buttonSave);
        profilePicture = findViewById(R.id.ProfilePicture);

        // Set up DatePickerDialog for DOB EditText
        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Set up profile picture click listener
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        // Set up Save button click listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        // Load user profile data
        loadUserProfile();
    }

    // Method to display DatePickerDialog
    private void showDatePicker() {
        mCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        updateDOBEditText();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Method to update DOB EditText with selected date
    private void updateDOBEditText() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDOB.setText(sdf.format(mCalendar.getTime()));
    }

    // Method to open image chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle result of image chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                profilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to save user profile data
    private void saveUserProfile() {
        final String name = editName.getText().toString().trim();
        final String age = editAge.getText().toString().trim();
        final String dob = editDOB.getText().toString().trim();
        final String emergencyContact = editEmergencyContact.getText().toString().trim();
        final String address = editAddress.getText().toString().trim();
        final String gender = radioButtonMale.isChecked() ? "Male" : "Female";
        final String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(dob)
                || TextUtils.isEmpty(emergencyContact) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageUri != null) {
            // Upload profile picture to Firebase Storage
            final StorageReference storageRef = mStorage.getReference().child("profile_pictures")
                    .child(mUser.getUid() + ".jpg");
            storageRef.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    mImageUrl = task.getResult().toString();
                                    updateUserProfile(name, age, dob, emergencyContact, address, gender, bloodGroup);
                                } else {
                                    Toast.makeText(UserProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            updateUserProfile(name, age, dob, emergencyContact, address, gender, bloodGroup);
        }
    }

    // Method to update user profile data in Firestore
    private void updateUserProfile(String name, String age, String dob, String emergencyContact,
                                   String address, String gender, String bloodGroup) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", name);
        profileData.put("age", age);
        profileData.put("dob", dob);
        profileData.put("emergencyContact", emergencyContact);
        profileData.put("address", address);
        profileData.put("gender", gender);
        profileData.put("bloodGroup", bloodGroup);
        if (mImageUrl != null) {
            profileData.put("profilePictureUrl", mImageUrl);
        }

        DocumentReference userProfileRef = mFirestore.collection("users").document(mUser.getUid());
        userProfileRef.set(profileData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to load user profile data from Firestore
    private void loadUserProfile() {
        DocumentReference userProfileRef = mFirestore.collection("users").document(mUser.getUid());
        userProfileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        editName.setText(document.getString("name"));
                        editAge.setText(document.getString("age"));
                        editDOB.setText(document.getString("dob"));
                        editEmergencyContact.setText(document.getString("emergencyContact"));
                        editAddress.setText(document.getString("address"));
                        String gender = document.getString("gender");
                        if ("Male".equals(gender)) {
                            radioButtonMale.setChecked(true);
                        } else {
                            radioButtonFemale.setChecked(true);
                        }
                        String bloodGroup = document.getString("bloodGroup");
                        // Set selection for spinner
                        // You need to implement this part as per your specific spinner setup
                        // For example:
                         ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UserProfileActivity.this,
                               R.array.blood_groups, android.R.layout.simple_spinner_item);
                          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                         spinnerBloodGroup.setAdapter(adapter);
                         spinnerBloodGroup.setSelection(adapter.getPosition(bloodGroup));
                        // Load profile picture using Glide if available
                        String profilePictureUrl = document.getString("profilePictureUrl");
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Glide.with(UserProfileActivity.this).load(profilePictureUrl).into(profilePicture);
                        }
                    }
                }
            }
        });
    }
}
