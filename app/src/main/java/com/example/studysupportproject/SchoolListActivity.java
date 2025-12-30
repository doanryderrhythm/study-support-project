package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SchoolListActivity extends AppCompatActivity implements SchoolAdapter.OnSchoolActionListener {

    private RecyclerView rvSchools;
    private FloatingActionButton fabAddSchool;
    private SchoolAdapter schoolAdapter;
    private DatabaseHelper dbHelper;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        dbHelper = new DatabaseHelper();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh sách trường học");
        }
        menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(View.GONE);

        // Setup views
        rvSchools = findViewById(R.id.rv_schools);
        fabAddSchool = findViewById(R.id.fab_add_school);

        // Setup RecyclerView
        rvSchools.setLayoutManager(new LinearLayoutManager(this));
        schoolAdapter = new SchoolAdapter(this);

        // Set click listener for school items
        schoolAdapter.setOnSchoolClickListener(school -> {
            Intent intent = new Intent(SchoolListActivity.this, SemesterListActivity.class);
            intent.putExtra("school_id", school.getId());
            intent.putExtra("school_name", school.getSchoolName());
            startActivity(intent);
        });

        // Set action listener for edit/delete buttons
        schoolAdapter.setOnSchoolActionListener(this);

        rvSchools.setAdapter(schoolAdapter);

        // Setup FAB
        fabAddSchool.setOnClickListener(v -> {
            Intent intent = new Intent(SchoolListActivity.this, SchoolDetailActivity.class);
            startActivityForResult(intent, 100);
        });

        loadSchools();
    }

    private void loadSchools() {
        new Thread(() -> {
            List<School> schools = dbHelper.getAllSchools();
            runOnUiThread(() -> {
                if (schools != null && !schools.isEmpty()) {
                    schoolAdapter.setSchools(schools);
                    rvSchools.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SchoolListActivity.this, "No schools found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onEditSchool(School school) {
        Intent intent = new Intent(SchoolListActivity.this, SchoolDetailActivity.class);
        intent.putExtra("school_id", school.getId());
        intent.putExtra("school_name", school.getSchoolName());
        startActivityForResult(intent, 100);
    }

    @Override
    public void onDeleteSchool(School school) {
        // Check if school has semesters attached
        new Thread(() -> {
            boolean hasSemesters = dbHelper.hasSemestersAttached(school.getId());
            runOnUiThread(() -> {
                if (hasSemesters) {
                    Toast.makeText(SchoolListActivity.this, "Cannot delete school - semesters are still attached", Toast.LENGTH_SHORT).show();
                } else {
                    // Show confirmation dialog
                    new AlertDialog.Builder(SchoolListActivity.this)
                            .setTitle("Delete School")
                            .setMessage("Are you sure you want to delete this school?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                new Thread(() -> {
                                    try {
                                        dbHelper.deleteSchool(school.getId());
                                        runOnUiThread(() -> {
                                            Toast.makeText(SchoolListActivity.this, "School deleted", Toast.LENGTH_SHORT).show();
                                            loadSchools();
                                        });
                                    } catch (Exception e) {
                                        Log.e("SchoolListActivity", "Error deleting school", e);
                                        runOnUiThread(() -> Toast.makeText(SchoolListActivity.this, "Error deleting school: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }
                                }).start();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadSchools();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
