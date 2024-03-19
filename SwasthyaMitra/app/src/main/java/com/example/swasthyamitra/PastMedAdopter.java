package com.example.swasthyamitra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PastMedAdopter extends RecyclerView.Adapter<PastMedAdopter.ViewHolder> {

    private Context mContext;
    private List<PastMedicine> mMedList;
    private SimpleDateFormat mDateFormat;
    private PastMedClickListener mListener;

    public PastMedAdopter(Context context, List<PastMedicine> medList) {
        mContext = context;
        mMedList = medList;
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (context instanceof PastMedAdopter.PastMedClickListener) {
            mListener = (PastMedAdopter.PastMedClickListener) context; // Cast context to listener interface
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CurrentMedClickListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PastMedicine pastMedicine = mMedList.get(position);
        holder.medicationNameTextView.setText(pastMedicine.getMedicationName());
        holder.dosageTextView.setText(mContext.getString(R.string.dosage_format, pastMedicine.getDosage()));
        holder.startDateTextView.setText(mContext.getString(R.string.start_date_format, mDateFormat.format(pastMedicine.getStartDate())));
        holder.endDateTextView.setText(mContext.getString(R.string.end_date_format, mDateFormat.format(pastMedicine.getEndDate())));

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPastMedClick(position);
            }
        });

        // Set long click listener
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onPastMedLongClick(position);
                return true; // Consume the long click event
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMedList.size();
    }

    public interface PastMedClickListener {
        void onPastMedClick(int position);
        void onPastMedLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicationNameTextView;
        TextView dosageTextView;
        TextView startDateTextView;
        TextView endDateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            medicationNameTextView = itemView.findViewById(R.id.PmedicineTitle);
            dosageTextView = itemView.findViewById(R.id.PmedicineDosage);
            startDateTextView = itemView.findViewById(R.id.PmedicineStartDate);
            endDateTextView = itemView.findViewById(R.id.PmedicineEndDate);
        }
    }
}
