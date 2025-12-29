package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.SemesterViewHolder> {
    private List<String> semesters;
    private Map<String, List<Grade>> gradesBySemester;

    public GradesAdapter(List<String> semesters, Map<String, List<Grade>> gradesBySemester) {
        this.semesters = semesters;
        this.gradesBySemester = gradesBySemester;
    }

    @Override
    public SemesterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semester_grades, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SemesterViewHolder holder, int position) {
        String semester = semesters.get(position);
        List<Grade> grades = gradesBySemester.get(semester);

        holder.semesterTitle.setText(semester);
        holder.gradesTable.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(holder.gradesTable.getContext());
        headerRow.setBackgroundColor(0xFFE8F5E9); // Light green background

        String[] headers = {"Subject", "Grade", "Type"};
        for (String header : headers) {
            TextView headerCell = createTableCell(holder.gradesTable.getContext(), header, true);
            headerRow.addView(headerCell);
        }
        holder.gradesTable.addView(headerRow);

        // Add grade rows
        if (grades != null) {
            for (Grade grade : grades) {
                TableRow row = new TableRow(holder.gradesTable.getContext());

                // Alternate row colors
                if (holder.gradesTable.getChildCount() % 2 == 0) {
                    row.setBackgroundColor(0xFFFAFAFA);
                }

                TextView subjectCell = createTableCell(holder.gradesTable.getContext(),
                        grade.getSubjectName(), false);
                TextView gradeCell = createTableCell(holder.gradesTable.getContext(),
                        String.format("%.2f", grade.getGradeValue()), false);
                TextView typeCell = createTableCell(holder.gradesTable.getContext(),
                        grade.getGradeType(), false);

                row.addView(subjectCell);
                row.addView(gradeCell);
                row.addView(typeCell);

                holder.gradesTable.addView(row);
            }
        }
    }

    @Override
    public int getItemCount() {
        return semesters.size();
    }

    private TextView createTableCell(android.content.Context context, String text, boolean isHeader) {
        TextView cell = new TextView(context);
        cell.setText(text);
        cell.setPadding(16, 12, 16, 12);
        cell.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        if (isHeader) {
            cell.setTextColor(0xFF1B5E20); // Dark green
            cell.setTextSize(14);
            cell.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            cell.setTextColor(0xFF424242);
            cell.setTextSize(13);
        }

        return cell;
    }

    public static class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView semesterTitle;
        TableLayout gradesTable;

        public SemesterViewHolder(View itemView) {
            super(itemView);
            semesterTitle = itemView.findViewById(R.id.semester_title);
            gradesTable = itemView.findViewById(R.id.grades_table);
        }
    }
}
