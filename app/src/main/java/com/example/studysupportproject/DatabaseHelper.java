package com.example.studysupportproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "StudySupportDB";

    // Table Names
    private static final String TABLE_USERS = "users";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // USERS Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_PHONE = "phone";

    // Table Create Statements
    // Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_FULL_NAME + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // create new tables
        onCreate(db);
    }

    // ------------------------ USERS TABLE METHODS -------------------------

    /**
     * Thêm người dùng mới
     */
    public long addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD, password);

        // insert row
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    /**
     * Thêm người dùng với đầy đủ thông tin
     */
    public long addUserWithDetails(String username, String email, String password, String fullName, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_FULL_NAME, fullName);
        values.put(KEY_PHONE, phone);

        // insert row
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa
     */
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Kiểm tra đăng nhập (chỉ bằng email)
     */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ? AND " + KEY_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Kiểm tra đăng nhập bằng username/email và password
     * Trả về User nếu đúng, null nếu sai
     */
    public User checkLogin(String usernameOrEmail, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Tìm user bằng username hoặc email và password
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE (" + KEY_USERNAME + " = ? OR " + KEY_EMAIL + " = ?) AND " +
                KEY_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail, password});

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));

            // Các trường khác nếu có
            int fullNameIndex = cursor.getColumnIndex(KEY_FULL_NAME);
            if (fullNameIndex != -1) {
                user.setFullName(cursor.getString(fullNameIndex));
            } else {
                user.setFullName("");
            }

            int phoneIndex = cursor.getColumnIndex(KEY_PHONE);
            if (phoneIndex != -1) {
                user.setPhone(cursor.getString(phoneIndex));
            } else {
                user.setPhone("");
            }

            int createdAtIndex = cursor.getColumnIndex(KEY_CREATED_AT);
            if (createdAtIndex != -1) {
                user.setCreatedAt(cursor.getString(createdAtIndex));
            } else {
                user.setCreatedAt("");
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return user;
    }

    /**
     * Lấy thông tin người dùng bằng email
     */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));

            int fullNameIndex = cursor.getColumnIndex(KEY_FULL_NAME);
            if (fullNameIndex != -1) {
                user.setFullName(cursor.getString(fullNameIndex));
            } else {
                user.setFullName("");
            }

            int phoneIndex = cursor.getColumnIndex(KEY_PHONE);
            if (phoneIndex != -1) {
                user.setPhone(cursor.getString(phoneIndex));
            } else {
                user.setPhone("");
            }

            int createdAtIndex = cursor.getColumnIndex(KEY_CREATED_AT);
            if (createdAtIndex != -1) {
                user.setCreatedAt(cursor.getString(createdAtIndex));
            } else {
                user.setCreatedAt("");
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return user;
    }

    /**
     * Cập nhật mật khẩu
     */
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USERS, values, KEY_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsAffected > 0;
    }

    /**
     * Cập nhật thông tin người dùng
     */
    public boolean updateUserProfile(String email, String fullName, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FULL_NAME, fullName);
        values.put(KEY_PHONE, phone);

        int rowsAffected = db.update(TABLE_USERS, values, KEY_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsAffected > 0;
    }

    /**
     * Lấy tất cả người dùng (cho admin)
     */
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    /**
     * Xóa người dùng
     */
    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_USERS, KEY_EMAIL + " = ?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    /**
     * Đếm số lượng người dùng
     */
    public int getUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Kiểm tra database có trống không
     */
    public boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }
    private void addTestUsers(SQLiteDatabase db) {
        // Tài khoản Admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(KEY_USERNAME, "admin");
        adminValues.put(KEY_EMAIL, "admin@studysupport.com");
        adminValues.put(KEY_PASSWORD, "Admin@123");
        adminValues.put(KEY_FULL_NAME, "Quản trị viên hệ thống");
        adminValues.put(KEY_PHONE, "0909123456");
        db.insert(TABLE_USERS, null, adminValues);

        // Tài khoản Giáo viên
        ContentValues teacherValues = new ContentValues();
        teacherValues.put(KEY_USERNAME, "teacher01");
        teacherValues.put(KEY_EMAIL, "teacher01@gmail.com");
        teacherValues.put(KEY_PASSWORD, "Teacher@123");
        teacherValues.put(KEY_FULL_NAME, "Nguyễn Văn A");
        teacherValues.put(KEY_PHONE, "0912345678");
        db.insert(TABLE_USERS, null, teacherValues);

        // Tài khoản Học sinh 1
        ContentValues student1Values = new ContentValues();
        student1Values.put(KEY_USERNAME, "student01");
        student1Values.put(KEY_EMAIL, "student01@gmail.com");
        student1Values.put(KEY_PASSWORD, "Student@123");
        student1Values.put(KEY_FULL_NAME, "Trần Thị B");
        student1Values.put(KEY_PHONE, "0923456789");
        db.insert(TABLE_USERS, null, student1Values);

        // Tài khoản Học sinh 2
        ContentValues student2Values = new ContentValues();
        student2Values.put(KEY_USERNAME, "student02");
        student2Values.put(KEY_EMAIL, "student02@gmail.com");
        student2Values.put(KEY_PASSWORD, "Password123");
        student2Values.put(KEY_FULL_NAME, "Lê Văn C");
        student2Values.put(KEY_PHONE, "0934567890");
        db.insert(TABLE_USERS, null, student2Values);
    }
    /**
     * Đóng database
     */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}  // <-- KẾT THÚC CLASS Ở ĐÂY, KHÔNG CÓ CLASS LỒNG NHAU