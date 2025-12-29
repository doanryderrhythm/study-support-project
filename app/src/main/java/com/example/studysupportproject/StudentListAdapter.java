package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {
    private List<User> students;
    private OnStudentClickListener onStudentClickListener;

    public interface OnStudentClickListener {
        void onStudentClick(User student);
    }

    public StudentListAdapter(List<User> students, OnStudentClickListener onStudentClickListener) {
        this.students = students;
        this.onStudentClickListener = onStudentClickListener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_list, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User student = students.get(position);
        holder.tvStudentName.setText(student.getFullName());
        holder.tvStudentId.setText("ID: " + student.getId());
        holder.tvStudentUsername.setText(student.getUsername());

        holder.itemView.setOnClickListener(v -> onStudentClickListener.onStudentClick(student));
    }

    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStudentName;
        public TextView tvStudentId;
        public TextView tvStudentUsername;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvStudentUsername = itemView.findViewById(R.id.tv_student_username);
        }
    }
}
