package com.example.studysupportproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private Context context;
    private List<Subject> subjects = new ArrayList<>();
    private OnSubjectDeleteListener deleteListener;
    private OnSubjectEditListener editListener;

    public SubjectAdapter(Context context) {
        this.context = context;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
        notifyDataSetChanged();
    }

    public void setOnSubjectDeleteListener(OnSubjectDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setOnSubjectEditListener(OnSubjectEditListener listener) {
        this.editListener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.tvSubjectName.setText(subject.getName());

        // Handle item click for editing
        holder.itemView.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onSubjectEdit(subject);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onSubjectDelete(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public interface OnSubjectDeleteListener {
        void onSubjectDelete(Subject subject);
    }

    public interface OnSubjectEditListener {
        void onSubjectEdit(Subject subject);
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName;
        ImageButton btnDelete;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            btnDelete = itemView.findViewById(R.id.btn_delete_subject);
        }
    }
}
