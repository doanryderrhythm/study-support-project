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

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {

    private List<School> schools;
    private OnSchoolClickListener onSchoolClickListener;
    private OnSchoolActionListener onSchoolActionListener;
    private Context context;

    public interface OnSchoolClickListener {
        void onSchoolClick(School school);
    }

    public interface OnSchoolActionListener {
        void onEditSchool(School school);
        void onDeleteSchool(School school);
    }

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

    public void setOnSchoolActionListener(OnSchoolActionListener listener) {
        this.onSchoolActionListener = listener;
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

        // Check if user is admin and show/hide admin actions
        boolean isAdmin = isUserAdmin();
        if (isAdmin) {
            holder.adminActions.setVisibility(View.VISIBLE);
        } else {
            holder.adminActions.setVisibility(View.GONE);
        }

        // Handle edit button click
        holder.btnEditSchool.setOnClickListener(v -> {
            if (onSchoolActionListener != null) {
                onSchoolActionListener.onEditSchool(school);
            }
        });

        // Handle delete button click
        holder.btnDeleteSchool.setOnClickListener(v -> {
            if (onSchoolActionListener != null) {
                onSchoolActionListener.onDeleteSchool(school);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onSchoolClickListener != null) {
                onSchoolClickListener.onSchoolClick(school);
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
        return schools != null ? schools.size() : 0;
    }

    static class SchoolViewHolder extends RecyclerView.ViewHolder {
        TextView tvSchoolName;
        LinearLayout adminActions;
        ImageButton btnEditSchool;
        ImageButton btnDeleteSchool;

        SchoolViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSchoolName = itemView.findViewById(R.id.tv_school_name);
            adminActions = itemView.findViewById(R.id.admin_actions);
            btnEditSchool = itemView.findViewById(R.id.btn_edit_school);
            btnDeleteSchool = itemView.findViewById(R.id.btn_delete_school);
        }
    }
}
