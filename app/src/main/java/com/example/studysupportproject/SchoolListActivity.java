package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SchoolListActivity extends AppCompatActivity {

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
