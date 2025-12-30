package com.example.studysupportproject;

import static android.view.View.GONE;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class SemesterListActivity extends AppCompatActivity implements SemesterAdapter.OnSemesterActionListener {

    private RecyclerView rvSemesters;
    private FloatingActionButton fabAddSemester;
    private SemesterAdapter semesterAdapter;
    private DatabaseHelper dbHelper;
    private int schoolId;
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private String schoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester_list);

        dbHelper = new DatabaseHelper();

        // Get data from intent
        schoolId = getIntent().getIntExtra("school_id", -1);
        schoolName = getIntent().getStringExtra("school_name");

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh sách học kỳ");
        }
        menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(GONE);

        // Setup views
        rvSemesters = findViewById(R.id.rv_semesters);
        fabAddSemester = findViewById(R.id.fab_add_semester);

        // Setup RecyclerView
        rvSemesters.setLayoutManager(new LinearLayoutManager(this));
        semesterAdapter = new SemesterAdapter(this);

        // Set click listener for semester items
        semesterAdapter.setOnSemesterClickListener(semester -> {
            Intent intent = new Intent(SemesterListActivity.this, ClassListActivity.class);
            intent.putExtra("semester_id", semester.getId());
            intent.putExtra("semester_name", semester.getName());
            intent.putExtra("school_id", schoolId);
            startActivity(intent);
        });

        // Set action listener for edit/delete buttons
        semesterAdapter.setOnSemesterActionListener(this);

        rvSemesters.setAdapter(semesterAdapter);

        // Setup FAB
        fabAddSemester.setOnClickListener(v -> {
            Intent intent = new Intent(SemesterListActivity.this, SemesterDetailActivity.class);
            intent.putExtra("school_id", schoolId);
            startActivityForResult(intent, 100);
        });

        // Setup Manage Subjects button
        Button btnManageSubjects = findViewById(R.id.btn_manage_subjects);
        if (btnManageSubjects != null) {
            btnManageSubjects.setOnClickListener(v -> {
                Intent intent = new Intent(SemesterListActivity.this, SubjectListActivity.class);
                intent.putExtra("school_id", schoolId);
                intent.putExtra("school_name", schoolName);
                startActivity(intent);
            });
        }

        loadSemesters();
    }

    private void loadSemesters() {
        new Thread(() -> {
            List<Semester> semesters = dbHelper.getSemestersBySchool(schoolId);
            runOnUiThread(() -> {
                if (semesters != null && !semesters.isEmpty()) {
                    semesterAdapter.setSemesters(semesters);
                    rvSemesters.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SemesterListActivity.this, "No semesters found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onEditSemester(Semester semester) {
        Intent intent = new Intent(SemesterListActivity.this, SemesterDetailActivity.class);
        intent.putExtra("school_id", schoolId);
        intent.putExtra("semester_id", semester.getId());
        intent.putExtra("semester_name", semester.getName());
        startActivityForResult(intent, 100);
    }

    @Override
    public void onDeleteSemester(Semester semester) {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Semester")
                .setMessage("Are you sure you want to delete this semester?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            dbHelper.deleteSemester(semester.getId());
                            runOnUiThread(() -> {
                                Toast.makeText(SemesterListActivity.this, "Semester deleted", Toast.LENGTH_SHORT).show();
                                loadSemesters();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(SemesterListActivity.this, "Error deleting semester: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadSemesters();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
