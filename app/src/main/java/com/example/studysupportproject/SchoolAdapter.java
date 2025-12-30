package com.example.studysupportproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {

    private List<School> schools;
    private OnSchoolClickListener onSchoolClickListener;
    private Context context;

    public SchoolAdapter(Context context) {
        this.context = context;
        this.schools = new ArrayList<>();
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
        notifyDataSetChanged();
    }

    public void setOnSchoolClickListener(OnSchoolClickListener listener) {
        this.onSchoolClickListener = listener;
    }

    @NonNull
    @Override
    public SchoolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_school, parent, false);
        return new SchoolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolViewHolder holder, int position) {
        School school = schools.get(position);
        holder.tvSchoolName.setText(school.getSchoolName());

        holder.itemView.setOnClickListener(v -> {
            if (onSchoolClickListener != null) {
                onSchoolClickListener.onSchoolClick(school);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schools != null ? schools.size() : 0;
    }

    public static class SchoolViewHolder extends RecyclerView.ViewHolder {
        TextView tvSchoolName;

        public SchoolViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSchoolName = itemView.findViewById(R.id.tv_school_name);
        }
    }

    public interface OnSchoolClickListener {
        void onSchoolClick(School school);
    }
}
