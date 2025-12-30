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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {

    private RecyclerView rvStudents;
    private FloatingActionButton fabAddStudent;
    private StudentAdapter studentAdapter;
    private ImageButton menuButton;
    private DatabaseHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        dbHelper = new DatabaseHelper();

        // Get data from intent
        classId = getIntent().getIntExtra("class_id", -1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Học sinh trong lớp");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(GONE);

        // Setup views
        rvStudents = findViewById(R.id.rv_students);
        fabAddStudent = findViewById(R.id.fab_add_student);

        // Setup RecyclerView
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter();
        studentAdapter.setOnStudentDeleteListener((student, position) -> {
            // Remove student from class
            new Thread(() -> {
                boolean success = dbHelper.removeStudentFromClass(classId, student.getId());
                runOnUiThread(() -> {
                    if (success) {
                        studentAdapter.removeStudent(position);
                        Toast.makeText(StudentListActivity.this, "Học sinh đã được xóa khỏi lớp", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StudentListActivity.this, "Không thể xóa học sinh", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
        rvStudents.setAdapter(studentAdapter);

        // FAB to add students
        fabAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(StudentListActivity.this, StudentSelectionActivity.class);
            intent.putExtra("class_id", classId);
            startActivityForResult(intent, 100);
        });

        loadStudents();
    }

    private void loadStudents() {
        new Thread(() -> {
            if (classId != -1) {
                List<User> students = dbHelper.getStudentsByClass(classId);
                runOnUiThread(() -> {
                    if (students != null && !students.isEmpty()) {
                        studentAdapter.setStudents(students);
                    } else {
                        Toast.makeText(StudentListActivity.this, "Không có học sinh trong lớp này", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadStudents();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
