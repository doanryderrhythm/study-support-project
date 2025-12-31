package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AccountMenuActivity extends AppCompatActivity {

//    private ImageButton btnBack;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private LinearLayout btnManageAccount, btnSocialSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        // Toolbar and navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Account Menu");
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        initializeViews();
        setupClickListeners();
        setupNavigationViewMenu();
    }

    private void initializeViews() {
//        btnBack = findViewById(R.id.btnBackAccountMenu);
        btnManageAccount = findViewById(R.id.btnManageAccount);
        btnSocialSettings = findViewById(R.id.btnSocialSettings);
    }

    private void setupClickListeners() {
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

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
    private void setupMenuButton() {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void setupNavigationViewMenu() {
        setupMenuButton();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_home) {
                    Intent intent = new Intent(AccountMenuActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_posts) {
                    Intent intent = new Intent(AccountMenuActivity.this, PostsActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_study) {
                    Intent intent;
                    User savedUser = SharedPrefManager.getInstance(AccountMenuActivity.this).getUser();
                    String userRole = savedUser != null ? savedUser.getRole() : "student";
                    if (userRole != null) {
                        if (userRole.equals("teacher") || userRole.equals("admin")) {
                            intent = new Intent(AccountMenuActivity.this, GradeManagementActivity.class);
                        } else {
                            intent = new Intent(AccountMenuActivity.this, StudentGradesActivity.class);
                        }
                        startActivity(intent);
                    } else {
                        Toast.makeText(AccountMenuActivity.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_account) {
                    // Account button - stay in AccountMenuActivity
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_logout) {
                    SharedPrefManager.getInstance(AccountMenuActivity.this).logout();
                    Intent intent = new Intent(AccountMenuActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                }

                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}