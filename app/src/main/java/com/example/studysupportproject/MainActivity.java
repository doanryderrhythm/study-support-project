package com.example.studysupportproject; // Thay bằng package của bạn

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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

        ConSQL c = new ConSQL();
        Connection connection = c.conclass();
        if (connection != null) {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            try {
                Statement smt = connection.createStatement();
                ResultSet set = smt.executeQuery("SELECT * FROM dbo.roles");
                while (set.next()) {
                    Log.d("Connected", set.getString(1));
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }
}