package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SocialNetworkSettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private SwitchCompat swFacebook, swTwitter, swGoogle, swLinkedin;
    private Button btnSaveSettings;
    private LinearLayout socialNetworksContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_network_settings);

        initializeViews();
        setupClickListeners();
        loadSettings();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBackSocialSettings);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        swFacebook = findViewById(R.id.swFacebook);
        swTwitter = findViewById(R.id.swTwitter);
        swGoogle = findViewById(R.id.swGoogle);
        swLinkedin = findViewById(R.id.swLinkedin);
        socialNetworksContainer = findViewById(R.id.socialNetworksContainer);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        // Tải cài đặt từ SharedPreferences (nếu có)
        // Tạm thời để mặc định là chưa kết nối
        swFacebook.setChecked(false);
        swTwitter.setChecked(false);
        swGoogle.setChecked(false);
        swLinkedin.setChecked(false);
    }

    private void saveSettings() {
        // Lưu cài đặt
        Toast.makeText(this, "Cài đặt mạng xã hội đã được lưu", Toast.LENGTH_SHORT).show();
        // Có thể thêm logic lưu vào database hoặc SharedPreferences ở đây
    }
}