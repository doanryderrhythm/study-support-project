package com.example.studysupportproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView middleTextView;

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
        Connection connection = c.conclass();
        if (connection != null) {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            try {
                Statement smt = connection.createStatement();
                ResultSet set = smt.executeQuery("SELECT * FROM dbo.grades");

                connection.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }
}