package com.example.swasthyamitra;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Appointment> appointmentList;
    private AppointmentClickListener listener;

    public AppointmentAdapter(List<Appointment> appointmentList, AppointmentAdapter.AppointmentClickListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.bind(appointment);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAppointmentClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onAppointmentLongClick(position);
                    return true; // Return true to indicate the event is consumed
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public interface AppointmentClickListener {
        void onAppointmentClick(int position);
        void onAppointmentLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView dateTimeTextView;
        private TextView locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.AppointmentTitle);
            dateTimeTextView = itemView.findViewById(R.id.appointmentdate);
            locationTextView = itemView.findViewById(R.id.AppointmentLocation);
        }

        public void bind(Appointment appointment) {
            titleTextView.setText(appointment.getTitle());
            String dateTime = DateFormat.getDateTimeInstance().format(appointment.getDateInMillis()) + " at " + DateFormat.getTimeInstance().format(appointment.getTimeInMillis());
            dateTimeTextView.setText(dateTime);
            locationTextView.setText(appointment.getLocation());
        }
    }
}
