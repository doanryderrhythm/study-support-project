package com.example.studysupportproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentSelectionActivity extends AppCompatActivity {

    private static final String TAG = "StudentSelectionActivity";

    private RecyclerView rvStudents;
    private Button btnAddSelected;
    private Button btnCancel;
    private ImageButton menuButton;

    private StudentSelectionAdapter studentAdapter;
    private DatabaseHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_selection);

        dbHelper = new DatabaseHelper();

        // Get data from intent
        classId = getIntent().getIntExtra("class_id", -1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chọn học sinh");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(ImageButton.GONE);

        // Setup views
        rvStudents = findViewById(R.id.rv_students_selection);
        btnAddSelected = findViewById(R.id.btn_add_selected);
        btnCancel = findViewById(R.id.btn_cancel);

        // Setup RecyclerView
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentSelectionAdapter();
        rvStudents.setAdapter(studentAdapter);

        // Add selected students button
        btnAddSelected.setOnClickListener(v -> addSelectedStudents());

        // Cancel button
        btnCancel.setOnClickListener(v -> onBackPressed());

        loadStudents();
    }

    private void loadStudents() {
        new Thread(() -> {
            // Get all students (users with student role) not yet in this class
            List<User> students = dbHelper.getStudentsNotInClass(classId);
            runOnUiThread(() -> {
                if (students != null && !students.isEmpty()) {
                    studentAdapter.setStudents(students);
                } else {
                    Toast.makeText(StudentSelectionActivity.this, "All students are already in this class", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void addSelectedStudents() {
        List<User> selectedStudents = studentAdapter.getSelectedStudents();
        if (selectedStudents.isEmpty()) {
            Toast.makeText(this, "Please select at least one student", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Adding " + selectedStudents.size() + " students to class " + classId);

        new Thread(() -> {
            int count = 0;
            int failed = 0;
            for (User student : selectedStudents) {
                try {
                    boolean success = dbHelper.addStudentToClass(classId, student.getId());
                    if (success) {
                        count++;
                        Log.i(TAG, "Successfully added student " + student.getId() + " (" + student.getFullName() + ") to class");
                    } else {
                        failed++;
                        Log.w(TAG, "Failed to add student " + student.getId() + " (" + student.getFullName() + ") to class");
                    }
                } catch (Exception e) {
                    failed++;
                    Log.e(TAG, "Exception adding student " + student.getId() + ": " + e.getMessage());
                }
            }

            final int finalCount = count;
            final int finalFailed = failed;
            runOnUiThread(() -> {
                if (finalFailed > 0) {
                    Toast.makeText(StudentSelectionActivity.this, "Added " + finalCount + " students, failed " + finalFailed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StudentSelectionActivity.this, "Added " + finalCount + " students successfully", Toast.LENGTH_SHORT).show();
                }
                if (finalCount > 0) {
                    setResult(RESULT_OK);
                    finish();
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
