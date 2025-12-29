package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private RecyclerView studentListRecyclerView;
    private StudentListAdapter studentListAdapter;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private ConSQL conSQL;
    private String semesterName;
    private String className;
    private int classId;
    private List<User> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grades_edit);

        semesterName = getIntent().getStringExtra("semester_name");
        className = getIntent().getStringExtra("class_name");
        classId = getIntent().getIntExtra("class_id", -1);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Grades - " + className);
        }

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        // Setup RecyclerView
        studentListRecyclerView = findViewById(R.id.student_grades_recycler_view);
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        conSQL = new ConSQL();
        students = new ArrayList<>();

        setupMenuButton();
        setupNavigationViewMenu();
        loadStudentsInClass();
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

    private void loadStudentsInClass() {
        new Thread(() -> {
            try {
                // Get all students in this class
                String query = "SELECT u.id, u.full_name, u.username, u.email " +
                        "FROM users u " +
                        "INNER JOIN class_students cs ON u.id = cs.student_id " +
                        "WHERE cs.class_id = " + classId + " " +
                        "ORDER BY u.full_name";

                List<java.util.Map<String, String>> results = conSQL.executeQuery(query);

                if (results != null) {
                    for (java.util.Map<String, String> row : results) {
                        User student = new User();
                        student.setId(Integer.parseInt(row.getOrDefault("id", "0")));
                        student.setFullName(row.getOrDefault("full_name", "Unknown"));
                        student.setUsername(row.getOrDefault("username", ""));
                        student.setEmail(row.getOrDefault("email", ""));
                        students.add(student);
                    }
                }

                runOnUiThread(() -> {
                    if (students.isEmpty()) {
                        Toast.makeText(StudentGradesEditActivity.this, "No students found in this class", Toast.LENGTH_SHORT).show();
                    } else {
                        studentListAdapter = new StudentListAdapter(students, student -> {
                            // Open grade editing page for this student
                            Intent intent = new Intent(StudentGradesEditActivity.this, StudentGradeDetailActivity.class);
                            intent.putExtra("student_id", student.getId());
                            intent.putExtra("student_name", student.getFullName());
                            intent.putExtra("class_id", classId);
                            intent.putExtra("class_name", className);
                            intent.putExtra("semester_name", semesterName);
                            startActivity(intent);
                        });
                        studentListRecyclerView.setAdapter(studentListAdapter);
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
