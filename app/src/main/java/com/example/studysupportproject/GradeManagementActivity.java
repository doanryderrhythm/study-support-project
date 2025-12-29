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

public class GradeManagementActivity extends AppCompatActivity {
    private RecyclerView semesterRecyclerView;
    private SemesterAdapter semesterAdapter;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private ConSQL conSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_management);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        // Setup RecyclerView
        semesterRecyclerView = findViewById(R.id.semester_recycler_view);
        semesterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        conSQL = new ConSQL();

        setupMenuButton();
        setupNavigationViewMenu();
        loadTeacherSemesters();
    }

    private void setupMenuButton() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
    }

    private void setupNavigationViewMenu() {
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(GradeManagementActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_posts) {
                Intent intent = new Intent(GradeManagementActivity.this, PostsActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_study) {
                // Stay in grade management
            } else if (itemId == R.id.nav_account) {
                Intent intent = new Intent(GradeManagementActivity.this, AccountMenuActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void loadTeacherSemesters() {
        new Thread(() -> {
            try {
                int teacherId = SharedPrefManager.getInstance(this).getUser().getId();

                String query = "SELECT DISTINCT s.semester_name " +
                        "FROM semesters s " +
                        "INNER JOIN classes c ON s.id = c.id " +
                        "INNER JOIN grades g ON g.class_id = c.id " +
                        "WHERE g.teacher_id = " + teacherId + " " +
                        "ORDER BY s.semester_name";

                List<String> semesters = new ArrayList<>();
                List<java.util.Map<String, String>> results = conSQL.executeQuery(query);

                if (results != null) {
                    for (java.util.Map<String, String> row : results) {
                        semesters.add(row.getOrDefault("semester_name", "Unknown"));
                    }
                }

                runOnUiThread(() -> {
                    if (semesters.isEmpty()) {
                        Toast.makeText(GradeManagementActivity.this, "No semesters found", Toast.LENGTH_SHORT).show();
                    } else {
                        semesterAdapter = new SemesterAdapter(semesters, semesterName -> {
                            Intent intent = new Intent(GradeManagementActivity.this, ClassListActivity.class);
                            intent.putExtra("semester_name", semesterName);
                            startActivity(intent);
                        });
                        semesterRecyclerView.setAdapter(semesterAdapter);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(GradeManagementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
