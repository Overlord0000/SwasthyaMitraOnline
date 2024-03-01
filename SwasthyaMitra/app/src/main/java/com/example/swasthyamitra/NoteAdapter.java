package com.example.swasthyamitra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context mContext;
    private List<Note> mNoteList;

    public NoteAdapter(Context context, List<Note> noteList) {
        mContext = context;
        mNoteList = noteList;
    }

    // ViewHolder class
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleoutput);
            descriptionTextView = itemView.findViewById(R.id.descriptionoutput);
        }
    }

    // Create ViewHolder instances
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.descriptionTextView.setText(note.getDescription());
    }

    // Return the size of the list
    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    // Update the list of notes
    public void setNotes(List<Note> noteList) {
        mNoteList = noteList;
        notifyDataSetChanged();
    }
}
