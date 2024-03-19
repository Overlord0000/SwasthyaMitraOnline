package com.example.swasthyamitra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class RecyclePastMedicineActivity extends AppCompatActivity implements PastMedAdopter.PastMedClickListener {

    private RecyclerView recyclerView;
    private PastMedAdopter pastMedAdapter;
    private List<PastMedicine> pastMedicineList;
    private FirebaseAuth mAuth;
    private DatabaseReference pastMedRef;
    private String userId;
    Button addpastmed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_past_medicine);

        recyclerView = findViewById(R.id.Pastrecyclerview);
        mAuth = FirebaseAuth.getInstance();
        pastMedicineList = new ArrayList<>();
        pastMedAdapter = new PastMedAdopter(this, pastMedicineList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(pastMedAdapter);
        addpastmed = findViewById(R.id.addnewPastbtn);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            pastMedRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("PastMedications");
            fetchPastMedications();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }
        addpastmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PastMedineActivity.class));

            }
        });
    }



    private void fetchPastMedications() {
        pastMedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pastMedicineList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PastMedicine pastMed = snapshot.getValue(PastMedicine.class);
                    if (pastMed != null) {
                        pastMedicineList.add(pastMed);
                    }
                }
                pastMedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecyclePastMedicineActivity.this, "Failed to fetch past medications: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPastMedClick(int position) {
        PastMedicine pastMedicine = pastMedicineList.get(position);
        Intent intent = new Intent(RecyclePastMedicineActivity.this, EditPastMedicineActivity.class);
        intent.putExtra("pastMedicineId", pastMedicine.getId());
        startActivity(intent);
    }

    @Override
    public void onPastMedLongClick(int position) {
        PastMedicine pastMedicine = pastMedicineList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this past medicine?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePastMedicine(pastMedicine.getId());
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

    private void deletePastMedicine(String pastMedicineId) {
        pastMedRef.child(pastMedicineId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RecyclePastMedicineActivity.this, "Past medicine deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecyclePastMedicineActivity.this, "Failed to delete past medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
