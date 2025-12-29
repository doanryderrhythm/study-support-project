package com.example.studysupportproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;
    private Context context;
    private OnScheduleClickListener onScheduleClickListener;

    public interface OnScheduleClickListener {
        void onScheduleClick(Schedule schedule);
    }

    public ScheduleAdapter(List<Schedule> scheduleList, Context context) {
        this.scheduleList = scheduleList;
        this.context = context;
    }

    public void setOnScheduleClickListener(OnScheduleClickListener listener) {
        this.onScheduleClickListener = listener;
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
        holder.tvDate.setText("Date: " + schedule.getScheduleDate());
        
        // Format time display
        String timeDisplay = "";
        if (schedule.getStartTime() != null && !schedule.getStartTime().isEmpty()) {
            timeDisplay = schedule.getStartTime();
        }
        if (schedule.getEndTime() != null && !schedule.getEndTime().isEmpty()) {
            if (!timeDisplay.isEmpty()) {
                timeDisplay += " - " + schedule.getEndTime();
            } else {
                timeDisplay = schedule.getEndTime();
            }
        }
        holder.tvTime.setText("Time: " + timeDisplay);
        
        holder.tvDescription.setText(schedule.getDescription());
        holder.tvType.setText("Type: " + (schedule.getScheduleType() != null ? schedule.getScheduleType() : "personal"));
        
        holder.itemView.setOnClickListener(v -> {
            if (onScheduleClickListener != null) {
                onScheduleClickListener.onScheduleClick(schedule);
            }
        });
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

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_schedule_title);
            tvDate = itemView.findViewById(R.id.tv_schedule_date);
            tvTime = itemView.findViewById(R.id.tv_schedule_time);
            tvDescription = itemView.findViewById(R.id.tv_schedule_description);
            tvType = itemView.findViewById(R.id.tv_schedule_type);
        }
    }
}
