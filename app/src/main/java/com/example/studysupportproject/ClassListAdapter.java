package com.example.studysupportproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ClassViewHolder> {

    private List<ClassItem> classes;
    private OnClassClickListener onClassClickListener;
    private OnClassActionListener onClassActionListener;
    private Context context;

    public ClassListAdapter(Context context) {
        this.context = context;
        this.classes = new ArrayList<>();
    }

    public void setClasses(List<ClassItem> classes) {
        this.classes = classes;
        notifyDataSetChanged();
    }

    public void setOnClassClickListener(OnClassClickListener listener) {
        this.onClassClickListener = listener;
    }

    public void setOnClassActionListener(OnClassActionListener listener) {
        this.onClassActionListener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class_list, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassItem classItem = classes.get(position);
        holder.tvClassName.setText(classItem.getClassName());
        
        if (classItem.getSubjectName() != null && !classItem.getSubjectName().isEmpty()) {
            holder.tvSubject.setText("Subject: " + classItem.getSubjectName());
            holder.tvSubject.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubject.setVisibility(View.GONE);
        }

        // Check if user is admin and show/hide admin actions
        boolean isAdmin = isUserAdmin();
        if (isAdmin) {
            holder.adminActions.setVisibility(View.VISIBLE);
        } else {
            holder.adminActions.setVisibility(View.GONE);
        }

        // Handle edit button click
        holder.btnEditClass.setOnClickListener(v -> {
            if (onClassActionListener != null) {
                onClassActionListener.onEditClass(classItem);
            }
        });

        // Handle delete button click
        holder.btnDeleteClass.setOnClickListener(v -> {
            if (onClassActionListener != null) {
                onClassActionListener.onDeleteClass(classItem);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onClassClickListener != null) {
                onClassClickListener.onClassClick(classItem);
            }
        });
    }

    private boolean isUserAdmin() {
        try {
            SharedPrefManager prefManager = SharedPrefManager.getInstance(context);
            User user = prefManager.getUser();
            if (user != null) {
                String role = user.getRole();
                return role != null && role.equals("admin");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return classes != null ? classes.size() : 0;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName;
        TextView tvSubject;
        LinearLayout adminActions;
        ImageButton btnEditClass;
        ImageButton btnDeleteClass;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            adminActions = itemView.findViewById(R.id.admin_actions);
            btnEditClass = itemView.findViewById(R.id.btn_edit_class);
            btnDeleteClass = itemView.findViewById(R.id.btn_delete_class);
        }
    }

    public interface OnClassClickListener {
        void onClassClick(ClassItem classItem);
    }

    public interface OnClassActionListener {
        void onEditClass(ClassItem classItem);
        void onDeleteClass(ClassItem classItem);
    }
}
