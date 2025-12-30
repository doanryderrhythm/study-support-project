package com.example.studysupportproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TeacherSelectionActivity extends AppCompatActivity {

    private RecyclerView rvTeachers;
    private Button btnAddSelected;
    private Button btnCancel;
    private StudentSelectionAdapter teacherSelectionAdapter;
    private DatabaseHelper dbHelper;
    private int classId;
    private List<User> availableTeachers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_selection);

        dbHelper = new DatabaseHelper();
        classId = getIntent().getIntExtra("class_id", -1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chọn giáo viên");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup views
        rvTeachers = findViewById(R.id.rv_teachers_selection);
        btnAddSelected = findViewById(R.id.btn_add_selected);
        btnCancel = findViewById(R.id.btn_cancel);

        // Setup RecyclerView
        rvTeachers.setLayoutManager(new LinearLayoutManager(this));
        availableTeachers = new ArrayList<>();
        teacherSelectionAdapter = new StudentSelectionAdapter();
        rvTeachers.setAdapter(teacherSelectionAdapter);

        // Add button
        btnAddSelected.setOnClickListener(v -> {
            List<User> selectedTeachers = teacherSelectionAdapter.getSelectedStudents();
            if (selectedTeachers.isEmpty()) {
                Toast.makeText(this, "Please select at least one teacher", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                for (User teacher : selectedTeachers) {
                    dbHelper.addTeacherToClass(classId, teacher.getId());
                }
                runOnUiThread(() -> {
                    Toast.makeText(TeacherSelectionActivity.this, "Teachers added successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }).start();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> onBackPressed());

        loadTeachers();
    }

    private void loadTeachers() {
        new Thread(() -> {
            List<User> teachers = dbHelper.getTeachersNotInClass(classId);
            runOnUiThread(() -> {
                if (teachers != null && !teachers.isEmpty()) {
                    teacherSelectionAdapter.setStudents(teachers);
                } else {
                    Toast.makeText(TeacherSelectionActivity.this, "Tất cả giáo viên đã được thêm vào lớp này", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
