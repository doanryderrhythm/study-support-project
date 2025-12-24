package com.example.studysupportproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "study_support_pref";
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_FULL_NAME = "key_full_name";
    private static final String KEY_IS_LOGGED_IN = "key_is_logged_in";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    // Lưu thông tin user khi đăng nhập
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        editor.apply();
    }

    // Kiểm tra đăng nhập
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy thông tin user hiện tại
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_USER_ID, -1));
        user.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setFullName(sharedPreferences.getString(KEY_FULL_NAME, null));

        return user;
    }

    // Đăng xuất
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}