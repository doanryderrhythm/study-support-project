package com.example.studysupportproject;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConSQL c = new ConSQL();
        connection = c.conclass();
        if (c != null) {
            try {
                String sqlstatement = "Select * from dbo.grades";
                Statement smt = connection.createStatement();
                ResultSet set = smt.executeQuery(sqlstatement);
                while (set.next()) {

                }
                connection.close();
            }
            catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
    }
}