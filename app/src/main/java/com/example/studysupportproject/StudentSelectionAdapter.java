package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentSelectionAdapter extends RecyclerView.Adapter<StudentSelectionAdapter.StudentViewHolder> {

    private List<User> students;
    private List<User> selectedStudents;

    public StudentSelectionAdapter() {
        this.students = new ArrayList<>();
        this.selectedStudents = new ArrayList<>();
    }

    public void setStudents(List<User> students) {
        this.students = students;
        this.selectedStudents.clear();
        notifyDataSetChanged();
    }

    public List<User> getSelectedStudents() {
        return new ArrayList<>(selectedStudents);
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_selection, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User student = students.get(position);
        holder.tvStudentName.setText(student.getFullName() != null ? student.getFullName() : "N/A");
        holder.tvStudentCode.setText("ID: " + student.getUsername());
        holder.tvEmail.setText(student.getEmail());

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(selectedStudents.contains(student));
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedStudents.contains(student)) {
                    selectedStudents.add(student);
                }
            } else {
                selectedStudents.remove(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        TextView tvStudentCode;
        TextView tvEmail;
        CheckBox cbSelect;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentCode = itemView.findViewById(R.id.tv_student_code);
            tvEmail = itemView.findViewById(R.id.tv_email);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }
    }
}
