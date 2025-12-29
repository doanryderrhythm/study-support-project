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
    private Map<String, Map<String, List<Grade>>> gradesBySemesterAndClass;

    public GradesAdapter(List<String> semesters, Map<String, Map<String, List<Grade>>> gradesBySemesterAndClass) {
        this.semesters = semesters;
        this.gradesBySemesterAndClass = gradesBySemesterAndClass;
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
        Map<String, List<Grade>> classesInSemester = gradesBySemesterAndClass.get(semester);

        holder.semesterTitle.setText(semester);
        holder.gradesTable.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(holder.gradesTable.getContext());
        headerRow.setBackgroundColor(0xFFE8F5E9); // Light green background

        String[] headers = {"Subject", "Quá trình", "Giữa kỳ", "Cuối kỳ", "Thực hành"};
        for (String header : headers) {
            TextView headerCell = createTableCell(holder.gradesTable.getContext(), header, true);
            headerRow.addView(headerCell);
        }
        holder.gradesTable.addView(headerRow);

        // Add class/subject rows
        if (classesInSemester != null) {
            int rowIndex = 0;
            for (String className : classesInSemester.keySet()) {
                List<Grade> gradesForClass = classesInSemester.get(className);
                
                // Create a map of grade types for this class
                Map<String, Double> gradeTypeMap = new java.util.HashMap<>();
                for (Grade grade : gradesForClass) {
                    gradeTypeMap.put(grade.getGradeType().toLowerCase(), grade.getGradeValue());
                }

                TableRow row = new TableRow(holder.gradesTable.getContext());

                // Alternate row colors
                if (rowIndex % 2 == 0) {
                    row.setBackgroundColor(0xFFFAFAFA);
                }

                // Subject/Class name cell
                TextView subjectCell = createTableCell(holder.gradesTable.getContext(),
                        className, false);
                row.addView(subjectCell);

                // Grade type cells in order: quá trình, giữa kỳ, cuối kỳ, thực hành
                String[] gradeTypes = {"quá trình", "giữa kỳ", "cuối kỳ", "thực hành"};
                for (String gradeType : gradeTypes) {
                    Double gradeValue = gradeTypeMap.get(gradeType.toLowerCase());
                    String gradeText = gradeValue != null ? String.format("%.2f", gradeValue) : "-";
                    TextView gradeCell = createTableCell(holder.gradesTable.getContext(),
                            gradeText, false);
                    row.addView(gradeCell);
                }

                holder.gradesTable.addView(row);
                rowIndex++;
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
        cell.setPadding(12, 10, 12, 10);
        cell.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        if (isHeader) {
            cell.setTextColor(0xFF1B5E20); // Dark green
            cell.setTextSize(12);
            cell.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            cell.setTextColor(0xFF424242);
            cell.setTextSize(12);
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
