package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<ClassItem> classes;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(ClassItem classItem);
    }

    public ClassAdapter(List<ClassItem> classes, OnClassClickListener listener) {
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
        ClassItem classItem = classes.get(position);
        holder.className.setText(classItem.getClassName());
        holder.itemView.setOnClickListener(v -> listener.onClassClick(classItem));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void updateList(List<ClassItem> newList) {
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
