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

import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {
    private List<Semester> semesters;
    private OnSemesterClickListener onSemesterClickListener;
    private OnSemesterActionListener onSemesterActionListener;
    private Context context;

    public interface OnSemesterClickListener {
        void onSemesterClick(Semester semester);
    }

    public interface OnSemesterActionListener {
        void onEditSemester(Semester semester);
        void onDeleteSemester(Semester semester);
    }

    public SemesterAdapter(Context context) {
        this.context = context;
        this.semesters = null;
    }

    public void setOnSemesterClickListener(OnSemesterClickListener listener) {
        this.onSemesterClickListener = listener;
    }

    public void setOnSemesterActionListener(OnSemesterActionListener listener) {
        this.onSemesterActionListener = listener;
    }

    public void setSemesters(List<Semester> semesters) {
        this.semesters = semesters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semester, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        Semester semester = semesters.get(position);
        holder.tvSemesterName.setText(semester.getName());

        // Check if user is admin and show/hide admin actions
        boolean isAdmin = isUserAdmin();
        if (isAdmin) {
            holder.adminActions.setVisibility(View.VISIBLE);
        } else {
            holder.adminActions.setVisibility(View.GONE);
        }

        // Handle edit button click
        holder.btnEditSemester.setOnClickListener(v -> {
            if (onSemesterActionListener != null) {
                onSemesterActionListener.onEditSemester(semester);
            }
        });

        // Handle delete button click
        holder.btnDeleteSemester.setOnClickListener(v -> {
            if (onSemesterActionListener != null) {
                onSemesterActionListener.onDeleteSemester(semester);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onSemesterClickListener != null) {
                onSemesterClickListener.onSemesterClick(semester);
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
        return semesters != null ? semesters.size() : 0;
    }

    static class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSemesterName;
        LinearLayout adminActions;
        ImageButton btnEditSemester;
        ImageButton btnDeleteSemester;

        SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemesterName = itemView.findViewById(R.id.tv_semester_name);
            adminActions = itemView.findViewById(R.id.admin_actions);
            btnEditSemester = itemView.findViewById(R.id.btn_edit_semester);
            btnDeleteSemester = itemView.findViewById(R.id.btn_delete_semester);
        }
    }
}
