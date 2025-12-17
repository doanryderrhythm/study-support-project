package com.example.studysupportproject; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private View navHome;
    private View navStudy;
    private View navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHome = findViewById(R.id.nav_home);
        navStudy = findViewById(R.id.nav_study);
        navProfile = findViewById(R.id.nav_profile);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Trang chủ", Toast.LENGTH_SHORT).show();
            }
        });

        navStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình học tập
                Toast.makeText(MainActivity.this, "Màn hình học tập", Toast.LENGTH_SHORT).show();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Màn hình hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}