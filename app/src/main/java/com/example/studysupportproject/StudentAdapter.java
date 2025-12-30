package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<User> students;
    private OnStudentDeleteListener onStudentDeleteListener;

    public StudentAdapter() {
        this.students = new ArrayList<>();
    }

    public void setStudents(List<User> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    public void setOnStudentDeleteListener(OnStudentDeleteListener listener) {
        this.onStudentDeleteListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User student = students.get(position);
        holder.tvStudentName.setText(student.getFullName() != null ? student.getFullName() : student.getUsername());
        holder.tvStudentEmail.setText(student.getEmail());

        holder.btnDeleteStudent.setOnClickListener(v -> {
            if (onStudentDeleteListener != null) {
                onStudentDeleteListener.onStudentDelete(student, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }

    public void removeStudent(int position) {
        students.remove(position);
        notifyItemRemoved(position);
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        TextView tvStudentEmail;
        ImageButton btnDeleteStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentEmail = itemView.findViewById(R.id.tv_student_email);
            btnDeleteStudent = itemView.findViewById(R.id.btn_delete_student);
        }
    }

    public interface OnStudentDeleteListener {
        void onStudentDelete(User student, int position);
    }
}
