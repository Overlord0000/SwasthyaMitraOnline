package com.example.swasthyamitra;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<NoteModel> noteList;
    private NoteClickListener listener;

    public NoteAdapter(List<NoteModel> noteList, NoteClickListener listener) {
        this.noteList = noteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteModel note = noteList.get(position);
        holder.bind(note);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNoteClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onNoteLongClick(position);
                    return true; // Return true to indicate the event is consumed
                }
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public interface NoteClickListener {
        void onNoteClick(int position);
        void onNoteLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView creationDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleoutput);
            descriptionTextView = itemView.findViewById(R.id.descriptionoutput);
            creationDateTextView = itemView.findViewById(R.id.dateoutput);
        }

        public void bind(NoteModel note) {
            titleTextView.setText(note.getTitle());
            descriptionTextView.setText(note.getDescription());
            String creationDateString = DateFormat.getDateInstance().format(note.getCreationDate());
            creationDateTextView.setText(creationDateString);
        }
    }
}
