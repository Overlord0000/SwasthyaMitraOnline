package com.example.swasthyamitra;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CurrentMedAdapter extends RecyclerView.Adapter<CurrentMedAdapter.ViewHolder> {

    private Context mContext;
    private List<CurrentMedModel> mMedList;
    private SimpleDateFormat mDateFormat;
    private CurrentMedClickListener mListener;

    public CurrentMedAdapter(Context context, List<CurrentMedModel> medList) {
        mContext = context;
        mMedList = medList;
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (context instanceof CurrentMedClickListener) {
            mListener = (CurrentMedClickListener) context; // Cast context to listener interface
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CurrentMedClickListener");
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_medicine_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CurrentMedModel currentMed = mMedList.get(position);
        holder.medicationNameTextView.setText(currentMed.getMedicationName());
        holder.dosageTextView.setText(mContext.getString(R.string.dosage_format, currentMed.getDosage()));
        holder.startDateTextView.setText(mContext.getString(R.string.start_date_format, mDateFormat.format(currentMed.getStartDate())));
        holder.endDateTextView.setText(mContext.getString(R.string.end_date_format, mDateFormat.format(currentMed.getEndDate())));

        // Set click and long-click listeners
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCurrentMedClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onCurrentMedLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMedList.size();
    }

    public interface CurrentMedClickListener {
        void onCurrentMedClick(int position);
        void onCurrentMedLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicationNameTextView;
        TextView dosageTextView;
        TextView startDateTextView;
        TextView endDateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            medicationNameTextView = itemView.findViewById(R.id.CmedicineTitle);
            dosageTextView = itemView.findViewById(R.id.CmedicineDosage);
            startDateTextView = itemView.findViewById(R.id.CmedicineStartDate);
            endDateTextView = itemView.findViewById(R.id.CmedicineEndDate);
        }
    }
}