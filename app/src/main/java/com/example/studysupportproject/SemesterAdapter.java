package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {
    private List<String> semesters;
    private OnSemesterClickListener listener;

    public interface OnSemesterClickListener {
        void onSemesterClick(String semesterName);
    }

    public SemesterAdapter(List<String> semesters, OnSemesterClickListener listener) {
        this.semesters = semesters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semester_selector, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        String semester = semesters.get(position);
        holder.semesterName.setText(semester);
        holder.itemView.setOnClickListener(v -> listener.onSemesterClick(semester));
    }

    @Override
    public int getItemCount() {
        return semesters.size();
    }

    public void updateList(List<String> newList) {
        this.semesters = newList;
        notifyDataSetChanged();
    }

    static class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView semesterName;

        SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            semesterName = itemView.findViewById(R.id.semester_name);
        }
    }
}
