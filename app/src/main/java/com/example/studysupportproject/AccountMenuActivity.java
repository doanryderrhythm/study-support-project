package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AccountMenuActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout btnManageAccount, btnSocialSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBackAccountMenu);
        btnManageAccount = findViewById(R.id.btnManageAccount);
        btnSocialSettings = findViewById(R.id.btnSocialSettings);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnManageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountMenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnSocialSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountMenuActivity.this, SocialNetworkSettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}