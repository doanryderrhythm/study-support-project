package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<Map<String, Object>> classes;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(Map<String, Object> classItem);
    }

    public ClassAdapter(List<Map<String, Object>> classes, OnClassClickListener listener) {
        this.classes = classes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_selector, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Map<String, Object> classItem = classes.get(position);
        holder.className.setText((String) classItem.get("name"));
        holder.itemView.setOnClickListener(v -> listener.onClassClick(classItem));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void updateList(List<Map<String, Object>> newList) {
        this.classes = newList;
        notifyDataSetChanged();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView className;

        ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
        }
    }
}
