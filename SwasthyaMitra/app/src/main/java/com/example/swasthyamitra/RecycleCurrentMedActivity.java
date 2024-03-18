package com.example.swasthyamitra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecycleCurrentMedActivity extends AppCompatActivity implements CurrentMedAdapter.CurrentMedClickListener {

    private RecyclerView recyclerView;
    private CurrentMedAdapter currentMedAdapter;
    private List<CurrentMedModel> currentMedList;
    private FirebaseAuth mAuth;
    private DatabaseReference currentMedRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_current_med);

        recyclerView = findViewById(R.id.recyclerviewCM);
        mAuth = FirebaseAuth.getInstance();
        currentMedList = new ArrayList<>();
        currentMedAdapter = new CurrentMedAdapter(this, currentMedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(currentMedAdapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId =(String) currentUser.getUid();
            currentMedRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("CurrentMedications");
            fetchCurrentMedications();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.addnewCurrentMedtbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecycleCurrentMedActivity.this, CurrentMedicineActivity.class));
            }
        });
    }

    private void fetchCurrentMedications() {
        currentMedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentMedList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CurrentMedModel currentMed = snapshot.getValue(CurrentMedModel.class);
                    if (currentMed != null) {
                        currentMedList.add(currentMed);
                    }
                }
                currentMedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecycleCurrentMedActivity.this, "Failed to fetch medications: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCurrentMedClick(int position) {
        // Handle item click by opening EditCurrentMedicineActivity
        CurrentMedModel currentMed = currentMedList.get(position);
        String currentMedId = currentMed.getId();
        Intent intent = new Intent(RecycleCurrentMedActivity.this, EditCurrentMedicineActivity.class);
        intent.putExtra("medicationId", currentMedId);
        startActivity(intent);
    }

    @Override
    public void onCurrentMedLongClick(int position) {
        // Handle long press by prompting the user to delete the current medicine
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCurrentMedicine(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void deleteCurrentMedicine(int position) {
        CurrentMedModel currentMed = currentMedList.get(position);
        String currentMedId = currentMed.getId();

        DatabaseReference currentMedRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("CurrentMedications").child(currentMedId);
        currentMedRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RecycleCurrentMedActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecycleCurrentMedActivity.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
