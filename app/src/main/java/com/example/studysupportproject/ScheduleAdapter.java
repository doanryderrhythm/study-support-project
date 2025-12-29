package com.example.studysupportproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;
    private Context context;
    private OnScheduleClickListener onScheduleClickListener;
    private OnScheduleDeleteListener onScheduleDeleteListener;

    public interface OnScheduleClickListener {
        void onScheduleClick(Schedule schedule);
    }

    public interface OnScheduleDeleteListener {
        void onScheduleDelete(Schedule schedule, int position);
    }

    public ScheduleAdapter(List<Schedule> scheduleList, Context context) {
        this.scheduleList = scheduleList;
        this.context = context;
    }

    public void setOnScheduleClickListener(OnScheduleClickListener listener) {
        this.onScheduleClickListener = listener;
    }

    public void setOnScheduleDeleteListener(OnScheduleDeleteListener listener) {
        this.onScheduleDeleteListener = listener;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        
        holder.tvTitle.setText(schedule.getTitle());
        
        // Format date display (convert YYYY-MM-DD to MMM DD, YYYY format)
        String formattedDate = formatDate(schedule.getScheduleDate());
        holder.tvDate.setText("Date: " + formattedDate);
        
        // Format time display
        String timeDisplay = "";
        if (schedule.getStartTime() != null && !schedule.getStartTime().isEmpty()) {
            timeDisplay = formatTime(schedule.getStartTime());
        }
        if (schedule.getEndTime() != null && !schedule.getEndTime().isEmpty()) {
            if (!timeDisplay.isEmpty()) {
                timeDisplay += " - " + formatTime(schedule.getEndTime());
            } else {
                timeDisplay = formatTime(schedule.getEndTime());
            }
        }
        holder.tvTime.setText("Time: " + timeDisplay);
        
        holder.tvDescription.setText(schedule.getDescription());
        holder.tvType.setText("Type: " + (schedule.getScheduleType() != null ? schedule.getScheduleType() : "personal"));
        
        // Schedule item click listener - for editing
        holder.itemView.setOnClickListener(v -> {
            if (onScheduleClickListener != null) {
                onScheduleClickListener.onScheduleClick(schedule);
            }
        });

        // Delete button click listener
        holder.btnDelete.setOnClickListener(v -> {
            if (onScheduleDeleteListener != null) {
                onScheduleDeleteListener.onScheduleDelete(schedule, position);
            }
        });
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr; // Return original if parsing fails
        }
    }

    private String formatTime(String timeStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date time = inputFormat.parse(timeStr);
            return outputFormat.format(time);
        } catch (ParseException e) {
            return timeStr; // Return original if parsing fails
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    public void updateList(List<Schedule> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvTime;
        public TextView tvDescription;
        public TextView tvType;
        public ImageButton btnDelete;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_schedule_title);
            tvDate = itemView.findViewById(R.id.tv_schedule_date);
            tvTime = itemView.findViewById(R.id.tv_schedule_time);
            tvDescription = itemView.findViewById(R.id.tv_schedule_description);
            tvType = itemView.findViewById(R.id.tv_schedule_type);
            btnDelete = itemView.findViewById(R.id.btn_delete_schedule);
        }
    }
}
