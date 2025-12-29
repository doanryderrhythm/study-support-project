package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentGradesActivity extends AppCompatActivity {
    private RecyclerView gradesRecyclerView;
    private GradesAdapter gradesAdapter;
    private ConSQL conSQL;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_grades);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Toolbar and navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        initializeViews();
        loadStudentGrades();
        setupMenuButton();
        setupNavigationViewMenu();
    }

    private void initializeViews() {
        gradesRecyclerView = findViewById(R.id.grades_recycler_view);
        gradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conSQL = new ConSQL();
    }

    private void loadStudentGrades() {
        // Get current user ID from SharedPrefManager
        int userId = SharedPrefManager.getInstance(this).getUser().getId();

        // Fetch grades from database
        List<Grade> allGrades = new ArrayList<>();
        List<String> semesters = new ArrayList<>();
        Map<String, List<Grade>> gradesBySemester = new HashMap<>();

        try {
            // Query: SELECT grades with semester information for current student
            String query = "SELECT g.id, g.student_id, g.class_id, g.subject_name, " +
                    "g.grade_value, g.grade_type, g.semester_id, s.semester_name, " +
                    "g.school_year, g.teacher_id, g.notes, g.created_at, g.updated_at " +
                    "FROM grades g " +
                    "LEFT JOIN semesters s ON g.semester_id = s.id " +
                    "WHERE g.student_id = " + userId + " " +
                    "ORDER BY g.semester_id DESC, g.subject_name ASC";

            List<Map<String, String>> result = conSQL.executeQuery(query);

            if (result != null && !result.isEmpty()) {
                for (Map<String, String> row : result) {
                    Grade grade = new Grade(
                            Integer.parseInt(row.getOrDefault("id", "0")),
                            Integer.parseInt(row.getOrDefault("student_id", "0")),
                            Integer.parseInt(row.getOrDefault("class_id", "0")),
                            row.getOrDefault("subject_name", ""),
                            Double.parseDouble(row.getOrDefault("grade_value", "0")),
                            row.getOrDefault("grade_type", ""),
                            Integer.parseInt(row.getOrDefault("semester_id", "0")),
                            row.getOrDefault("semester_name", "Unknown Semester"),
                            row.getOrDefault("school_year", ""),
                            Integer.parseInt(row.getOrDefault("teacher_id", "0")),
                            row.getOrDefault("notes", ""),
                            row.getOrDefault("created_at", ""),
                            row.getOrDefault("updated_at", "")
                    );

                    allGrades.add(grade);

                    // Group by semester
                    String semesterKey = grade.getSemesterName() + " (" + grade.getSchoolYear() + ")";
                    if (!semesters.contains(semesterKey)) {
                        semesters.add(semesterKey);
                    }

                    gradesBySemester.computeIfAbsent(semesterKey, k -> new ArrayList<>()).add(grade);
                }

                // Set adapter
                gradesAdapter = new GradesAdapter(semesters, gradesBySemester);
                gradesRecyclerView.setAdapter(gradesAdapter);
            } else {
                Toast.makeText(this, "No grades found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading grades: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void setupMenuButton() {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void setupNavigationViewMenu() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_home) {
                    Intent intent = new Intent(StudentGradesActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_posts) {
                    Intent intent = new Intent(StudentGradesActivity.this, PostsActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_study) {
                    //Study button - stay in StudentGradesActivity
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.nav_account) {
                    Intent intent = new Intent(StudentGradesActivity.this, AccountMenuActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                }

                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
