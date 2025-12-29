package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class StudentGradesEditActivity extends AppCompatActivity {
    private RecyclerView studentGradesRecyclerView;
    private StudentGradeEditAdapter gradeEditAdapter;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private Button submitButton;
    private ConSQL conSQL;
    private String semesterName;
    private String className;
    private List<Grade> grades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grades_edit);

        semesterName = getIntent().getStringExtra("semester_name");
        className = getIntent().getStringExtra("class_name");

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(className);
        }

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);
        submitButton = findViewById(R.id.submit_button);

        // Setup RecyclerView
        studentGradesRecyclerView = findViewById(R.id.student_grades_recycler_view);
        studentGradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        conSQL = new ConSQL();
        grades = new ArrayList<>();

        setupMenuButton();
        setupNavigationViewMenu();
        setupSubmitButton();
        loadStudentGrades();
    }

    private void setupMenuButton() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
    }

    private void setupNavigationViewMenu() {
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(StudentGradesEditActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_posts) {
                Intent intent = new Intent(StudentGradesEditActivity.this, PostsActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_study) {
                Intent intent = new Intent(StudentGradesEditActivity.this, GradeManagementActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_account) {
                Intent intent = new Intent(StudentGradesEditActivity.this, AccountMenuActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> submitGrades());
    }

    private void loadStudentGrades() {
        new Thread(() -> {
            try {
                int teacherId = SharedPrefManager.getInstance(this).getUser().getId();

                String query = "SELECT g.id, g.student_id, g.class_id, g.subject_name, " +
                        "g.grade_value, g.grade_type, g.semester_id, s.semester_name, " +
                        "g.school_year, g.teacher_id, g.notes, g.created_at, g.updated_at " +
                        "FROM grades g " +
                        "LEFT JOIN semesters s ON g.semester_id = s.id " +
                        "LEFT JOIN classes c ON g.class_id = c.id " +
                        "WHERE g.teacher_id = " + teacherId + " " +
                        "AND c.class_name = '" + className + "' " +
                        "AND s.semester_name = '" + semesterName + "' " +
                        "ORDER BY g.student_id, g.subject_name";

                List<java.util.Map<String, String>> results = conSQL.executeQuery(query);

                if (results != null) {
                    for (java.util.Map<String, String> row : results) {
                        Grade grade = new Grade(
                                Integer.parseInt(row.getOrDefault("id", "0")),
                                Integer.parseInt(row.getOrDefault("student_id", "0")),
                                Integer.parseInt(row.getOrDefault("class_id", "0")),
                                row.getOrDefault("subject_name", ""),
                                Double.parseDouble(row.getOrDefault("grade_value", "0")),
                                row.getOrDefault("grade_type", ""),
                                Integer.parseInt(row.getOrDefault("semester_id", "0")),
                                row.getOrDefault("semester_name", ""),
                                row.getOrDefault("school_year", ""),
                                Integer.parseInt(row.getOrDefault("teacher_id", "0")),
                                row.getOrDefault("notes", ""),
                                row.getOrDefault("created_at", ""),
                                row.getOrDefault("updated_at", "")
                        );
                        grades.add(grade);
                    }
                }

                runOnUiThread(() -> {
                    if (grades.isEmpty()) {
                        Toast.makeText(StudentGradesEditActivity.this, "No grades found", Toast.LENGTH_SHORT).show();
                    } else {
                        gradeEditAdapter = new StudentGradeEditAdapter(grades, (grade, position) -> {
                            // Grade updated in adapter
                            Toast.makeText(StudentGradesEditActivity.this,
                                    "Grade updated for student: " + grade.getStudentId(),
                                    Toast.LENGTH_SHORT).show();
                        });
                        studentGradesRecyclerView.setAdapter(gradeEditAdapter);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(StudentGradesEditActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void submitGrades() {
        new Thread(() -> {
            try {
                boolean allSuccess = true;
                for (Grade grade : grades) {
                    String updateQuery = "UPDATE grades SET grade_value = " + grade.getGradeValue() + 
                            " WHERE id = " + grade.getId();
                    boolean success = conSQL.executeUpdate(updateQuery);
                    if (!success) {
                        allSuccess = false;
                    }
                }
                boolean finalAllSuccess = allSuccess;
                runOnUiThread(() -> {
                    if (finalAllSuccess) {
                        Toast.makeText(StudentGradesEditActivity.this, "Grades submitted successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(StudentGradesEditActivity.this, "Some grades failed to submit", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(StudentGradesEditActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
