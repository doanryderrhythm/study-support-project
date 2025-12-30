package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TeacherListActivity extends AppCompatActivity {

    private RecyclerView rvTeachers;
    private FloatingActionButton fabAddTeacher;
    private StudentAdapter teacherAdapter;
    private ImageButton menuButton;
    private DatabaseHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        dbHelper = new DatabaseHelper();

        // Get data from intent
        classId = getIntent().getIntExtra("class_id", -1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Giáo viên trong lớp");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(ImageButton.GONE);

        // Setup views
        rvTeachers = findViewById(R.id.rv_teachers);
        fabAddTeacher = findViewById(R.id.fab_add_teacher);

        // Setup RecyclerView
        rvTeachers.setLayoutManager(new LinearLayoutManager(this));
        teacherAdapter = new StudentAdapter();
        teacherAdapter.setOnStudentDeleteListener((teacher, position) -> {
            // Remove teacher from class
            new Thread(() -> {
                boolean success = dbHelper.removeTeacherFromClass(classId, teacher.getId());
                runOnUiThread(() -> {
                    if (success) {
                        teacherAdapter.removeStudent(position);
                        Toast.makeText(TeacherListActivity.this, "Giáo viên đã được xóa khỏi lớp", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TeacherListActivity.this, "Không thể xóa giáo viên", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
        rvTeachers.setAdapter(teacherAdapter);

        // FAB to add teachers
        fabAddTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherListActivity.this, TeacherSelectionActivity.class);
            intent.putExtra("class_id", classId);
            startActivityForResult(intent, 100);
        });

        loadTeachers();
    }

    private void loadTeachers() {
        new Thread(() -> {
            if (classId != -1) {
                List<User> teachers = dbHelper.getTeachersByClass(classId);
                runOnUiThread(() -> {
                    if (teachers != null && !teachers.isEmpty()) {
                        teacherAdapter.setStudents(teachers);
                    } else {
                        Toast.makeText(TeacherListActivity.this, "Không có giáo viên trong lớp này", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadTeachers();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
