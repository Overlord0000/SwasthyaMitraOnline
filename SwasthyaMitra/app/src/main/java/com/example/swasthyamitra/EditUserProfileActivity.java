package com.example.swasthyamitra;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditUserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 103;

    private EditText editName, editAge, editDOB, editEmergencyContact, editAddress;
    private Spinner spinnerBloodGroup;
    private RadioGroup radioGroupGender;
    private Button btnSave;
    private ImageView profilePicture;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private Uri imageUri;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("UserProfile");
        storageRef = FirebaseStorage.getInstance().getReference().child("profile_pictures");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        editName = findViewById(R.id.EdEditName);
        editAge = findViewById(R.id.EdEditAge);
        editDOB = findViewById(R.id.EdEditDOB);
        editEmergencyContact = findViewById(R.id.EdEditEmergencyContact);
        editAddress = findViewById(R.id.EdEditAddress);
        spinnerBloodGroup = findViewById(R.id.EdspinnerBloodGroup);
        radioGroupGender = findViewById(R.id.EdradioGroupGender);
        btnSave = findViewById(R.id.buttonUpdate);
        profilePicture = findViewById(R.id.EdProfilePicture);

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
                openGallery();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        retrieveUserProfile();
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

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_PICK && data != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);
        }
    }

    private void retrieveUserProfile() {
        databaseRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        editName.setText(userProfile.getName());
                        editAge.setText(userProfile.getAge());
                        editDOB.setText(userProfile.getDob());
                        editEmergencyContact.setText(userProfile.getEmergencyContact());
                        editAddress.setText(userProfile.getAddress());

                        String bloodGroup = userProfile.getBloodGroup();
                        if (!TextUtils.isEmpty(bloodGroup)) {
                            int position = ((ArrayAdapter<String>) spinnerBloodGroup.getAdapter()).getPosition(bloodGroup);
                            spinnerBloodGroup.setSelection(position);
                        }

                        String gender = userProfile.getGender();
                        if ("Male".equals(gender)) {
                            radioGroupGender.check(R.id.EdradioButtonMale);
                        } else if ("Female".equals(gender)) {
                            radioGroupGender.check(R.id.EdradioButtonFemale);
                        }

                        if (!TextUtils.isEmpty(userProfile.getProfilePicture())) {
                            Glide.with(EditUserProfileActivity.this)
                                    .load(userProfile.getProfilePicture())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profilePicture);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditUserProfileActivity.this, "Failed to retrieve user profile", Toast.LENGTH_SHORT).show();
            }
        });
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
                TextUtils.isEmpty(emergencyContact) || TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(bloodGroup) || TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = databaseRef.child(currentUserId);
        userRef.child("name").setValue(name);
        userRef.child("age").setValue(age);
        userRef.child("dob").setValue(dob);
        userRef.child("emergencyContact").setValue(emergencyContact);
        userRef.child("address").setValue(address);
        userRef.child("bloodGroup").setValue(bloodGroup);
        userRef.child("gender").setValue(gender);

        if (imageUri != null) {
            final StorageReference profilePicRef = storageRef.child(currentUserId + "_profile_picture.jpg");
            profilePicRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        userRef.child("profilePicture").setValue(uri.toString());
                        Toast.makeText(EditUserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EditUserProfileActivity.this, "Failed to upload profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
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
