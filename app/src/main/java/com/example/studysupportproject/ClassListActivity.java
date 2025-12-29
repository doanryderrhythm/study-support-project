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

public class ClassListActivity extends AppCompatActivity {
    private RecyclerView classRecyclerView;
    private ClassAdapter classAdapter;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private ConSQL conSQL;
    private String semesterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        semesterName = getIntent().getStringExtra("semester_name");

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Classes - " + semesterName);
        }

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        // Setup RecyclerView
        classRecyclerView = findViewById(R.id.class_recycler_view);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        conSQL = new ConSQL();

        setupMenuButton();
        setupNavigationViewMenu();
        loadClassesForSemester();
    }

    private void setupMenuButton() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
    }

    private void setupNavigationViewMenu() {
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(ClassListActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_posts) {
                Intent intent = new Intent(ClassListActivity.this, PostsActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_study) {
                Intent intent = new Intent(ClassListActivity.this, GradeManagementActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_account) {
                Intent intent = new Intent(ClassListActivity.this, AccountMenuActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void loadClassesForSemester() {
        new Thread(() -> {
            try {
                int teacherId = SharedPrefManager.getInstance(this).getUser().getId();

                String query = "SELECT DISTINCT c.class_name, c.id " +
                        "FROM classes c " +
                        "INNER JOIN class_teachers ct ON c.id = ct.class_id " +
                        "WHERE ct.teacher_id = " + teacherId + " " +
                        "AND c.semester_id = (SELECT id FROM semesters WHERE semester_name = '" + semesterName + "') " +
                        "ORDER BY c.class_name";

                List<String> classes = new ArrayList<>();
                List<java.util.Map<String, String>> results = conSQL.executeQuery(query);

                if (results != null) {
                    for (java.util.Map<String, String> row : results) {
                        classes.add(row.getOrDefault("class_name", "Unknown"));
                    }
                }

                runOnUiThread(() -> {
                    if (classes.isEmpty()) {
                        Toast.makeText(ClassListActivity.this, "No classes found", Toast.LENGTH_SHORT).show();
                    } else {
                        classAdapter = new ClassAdapter(classes, className -> {
                            Intent intent = new Intent(ClassListActivity.this, StudentGradesEditActivity.class);
                            intent.putExtra("semester_name", semesterName);
                            intent.putExtra("class_name", className);
                            startActivity(intent);
                        });
                        classRecyclerView.setAdapter(classAdapter);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(ClassListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
