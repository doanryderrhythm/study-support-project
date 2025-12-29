package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentGradeEditAdapter extends RecyclerView.Adapter<StudentGradeEditAdapter.StudentGradeViewHolder> {
    private List<Grade> grades;
    private OnGradeEditListener listener;

    public interface OnGradeEditListener {
        void onGradeUpdate(Grade grade, int position);
    }

    public StudentGradeEditAdapter(List<Grade> grades, OnGradeEditListener listener) {
        this.grades = grades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentGradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_grade_edit, parent, false);
        return new StudentGradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentGradeViewHolder holder, int position) {
        Grade grade = grades.get(position);
        holder.studentName.setText("Student ID: " + grade.getStudentId());
        holder.subjectName.setText(grade.getSubjectName());
        holder.gradeInput.setText(String.valueOf(grade.getGradeValue()));
        holder.gradeType.setText(grade.getGradeType());

        holder.saveButton.setOnClickListener(v -> {
            try {
                double newGrade = Double.parseDouble(holder.gradeInput.getText().toString());
                grade.setGradeValue(newGrade);
                listener.onGradeUpdate(grade, position);
            } catch (NumberFormatException e) {
                holder.gradeInput.setError("Invalid grade");
            }
        });
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    public void updateList(List<Grade> newList) {
        this.grades = newList;
        notifyDataSetChanged();
    }

    static class StudentGradeViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        TextView subjectName;
        TextView gradeType;
        EditText gradeInput;
        Button saveButton;

        StudentGradeViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.student_name);
            subjectName = itemView.findViewById(R.id.subject_name);
            gradeType = itemView.findViewById(R.id.grade_type);
            gradeInput = itemView.findViewById(R.id.grade_input);
            saveButton = itemView.findViewById(R.id.save_button);
        }
    }
}
