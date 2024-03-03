package com.example.swasthyamitra;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ListDoctersNoteActivity extends AppCompatActivity implements NoteAdapter.NoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<NoteModel> noteList;
    private MaterialButton addNewNoteBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference notesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_docters_note);

        recyclerView = findViewById(R.id.recyclerview);
        addNewNoteBtn = findViewById(R.id.addnewnotebtn);
        mAuth = FirebaseAuth.getInstance();
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login activity or handle authentication
            // based on your app's requirements
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            String userId = currentUser.getUid();
            notesRef = FirebaseDatabase.getInstance().getReference("DoctorsNote").child(userId).child("notes");
            fetchNotes();
        }

        addNewNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListDoctersNoteActivity.this, DoctorsNoteActivity.class));
            }
        });
    }

    private void fetchNotes() {
        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoteModel note = snapshot.getValue(NoteModel.class);
                    if (note != null) {
                        noteList.add(note);
                    }
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListDoctersNoteActivity.this, "Failed to fetch notes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNoteClick(int position) {
        NoteModel clickedNote = noteList.get(position);
        Intent intent = new Intent(ListDoctersNoteActivity.this, EditDoctorsNoteActivity.class);
        intent.putExtra("noteId", clickedNote.getId());
        startActivity(intent);
    }


    @Override
    public void onNoteLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNote(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteNote(int position) {
        NoteModel noteToDelete = noteList.get(position);
        String noteId = noteToDelete.getId();
        DatabaseReference noteRef = notesRef.child(noteId);
        noteRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Toast.makeText(ListDoctersNoteActivity.this, "Failed to delete note: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListDoctersNoteActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
