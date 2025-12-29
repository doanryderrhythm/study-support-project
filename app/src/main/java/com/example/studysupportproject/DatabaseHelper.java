package com.example.studysupportproject;

import android.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";
    private ConSQL conSQL;

    public DatabaseHelper() {
        conSQL = new ConSQL();
    }

    // ------------------------ USERS TABLE METHODS -------------------------

    /**
     * Thêm người dùng mới
     */
    public long addUser(String username, String email, String password, String fullName, String phoneNumber, String dateOfBirth, String address) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long id = -1;

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null");
                return -1;
            }

            // Lấy class_id mặc định (giả sử class_id = 1, role_id = 3)
            String query = "INSERT INTO users (username, email, password, class_id, role_id, full_name, phone, date_of_birth, address) " +
                    "VALUES (?, ?, ?, 1, 3, ?, ?, ?, ?); SELECT SCOPE_IDENTITY() as id";

            stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, fullName);
            stmt.setString(5, phoneNumber);
            stmt.setString(6, dateOfBirth);
            stmt.setString(7, address);

            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding user: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return id;
    }

    /**
     * Thêm người dùng với đầy đủ thông tin
     */
    public long addUserWithDetails(String username, String email, String password, String fullName, String phone) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long id = -1;

        try {
            con = conSQL.conclass();
            if (con == null) return -1;

            String query = "INSERT INTO users (username, email, password, full_name, phone, class_id) " +
                    "VALUES (?, ?, ?, ?, ?, 1); SELECT SCOPE_IDENTITY() as id";

            stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, fullName);
            stmt.setString(5, phone);

            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding user with details: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return id;
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa
     */
    public boolean checkUsernameExists(String username) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "SELECT COUNT(*) as count FROM users WHERE username = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, username);

            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking username: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return exists;
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean checkEmailExists(String email) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "SELECT COUNT(*) as count FROM users WHERE email = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking email: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return exists;
    }

    /**
     * Kiểm tra đăng nhập (chỉ bằng email)
     */
    public boolean checkUser(String email, String password) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "SELECT COUNT(*) as count FROM users WHERE email = ? AND password = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking user: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return exists;
    }

    /**
     * Kiểm tra đăng nhập bằng username/email và password
     * Trả về User nếu đúng, null nếu sai
     */
    public User checkLogin(String usernameOrEmail, String password) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            con = conSQL.conclass();
            if (con == null) return null;

            String query = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);

            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name") != null ? rs.getString("full_name") : "");
                user.setPhone(rs.getString("phone") != null ? rs.getString("phone") : "");
                user.setCreatedAt(rs.getString("created_at") != null ? rs.getString("created_at") : "");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking login: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return user;
    }

    /**
     * Lấy thông tin người dùng bằng email
     */
    public User getUserByEmail(String email) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            con = conSQL.conclass();
            if (con == null) return null;

            String query = "SELECT * FROM users WHERE email = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name") != null ? rs.getString("full_name") : "");
                user.setAvatar(rs.getString("avatar") != null ? rs.getString("avatar") : "");
                user.setPhone(rs.getString("phone") != null ? rs.getString("phone") : "");
                user.setCreatedAt(rs.getString("created_at") != null ? rs.getString("created_at") : "");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting user by email: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return user;
    }

    /**
     * Lấy user bằng ID
     */
    public User getUserById(int user_id) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            con = conSQL.conclass();
            if (con == null) return null;

            String query = "SELECT u.*, r.role_name FROM users u LEFT JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, user_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name") != null ? rs.getString("full_name") : "");
                user.setAvatar(rs.getString("avatar") != null ? rs.getString("avatar") : "");
                user.setPhone(rs.getString("phone") != null ? rs.getString("phone") : "");
                user.setCreatedAt(rs.getString("created_at") != null ? rs.getString("created_at") : "");
                user.setRole(rs.getString("role_name") != null ? rs.getString("role_name") : "student");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting user by id: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return user;
    }

    /**
     * Cập nhật mật khẩu
     */
    public boolean updatePassword(String email, String newPassword) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "UPDATE users SET password = ?, updated_at = GETDATE() WHERE email = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating password: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Cập nhật thông tin người dùng
     */
    public boolean updateUserProfile(String email, String fullName, String phone) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "UPDATE users SET full_name = ?, phone = ?, updated_at = GETDATE() WHERE email = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, email);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating user profile: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Xóa người dùng
     */
    public boolean deleteUser(String email) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "DELETE FROM users WHERE email = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting user: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Đếm số lượng người dùng
     */
    public int getUsersCount() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            con = conSQL.conclass();
            if (con == null) return 0;

            String query = "SELECT COUNT(*) as count FROM users";
            stmt = con.prepareStatement(query);

            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting users count: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return count;
    }

    /**
     * Tạo post
     */
    public long createPost(String title, String content, int authorId, String postType) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long postId = -1;

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null");
                return -1;
            }

            String query = "{CALL CreatePost(?, ?, ?, ?)}";
            stmt = con.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, authorId);
            stmt.setString(4, postType);

            rs = stmt.executeQuery();
            if (rs.next()) {
                postId = rs.getLong("post_id");
                Log.i(TAG, "Post created successfully with ID: " + postId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error creating post: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return postId;
    }

    /**
     * Cập nhật post với post_type
     */
    public boolean updatePost(int postId, String title, String content, String postType) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null");
                return false;
            }

            String query = "{CALL UpdatePost(?, ?, ?, ?)}";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, postId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.setString(4, postType);

            boolean hasResults = stmt.execute();

            if (hasResults) {
                rs = stmt.getResultSet();
                if (rs != null && rs.next()) {
                    int result = rs.getInt("result");
                    String message = rs.getString("message");
                    success = result > 0;
                    Log.i(TAG, "Stored procedure result: " + result + ", message: " + message);
                }
            } else {
                int updateCount = stmt.getUpdateCount();
                success = updateCount > 0;
                Log.i(TAG, "Update count: " + updateCount);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating post: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, con);
        }
        return success;
    }

    /**
     * Lấy tất cả các bài viết
     */
    public List<Post> getAllPosts() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Post> posts = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) return posts;

            String query = "SELECT p.*, u.username, u.full_name " +
                        "FROM posts p " +
                        "LEFT JOIN users u ON p.author_id = u.id " +
                        "ORDER BY p.created_at DESC";
            stmt = con.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("author_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("post_type"),
                        rs.getBoolean("is_published"),
                        rs.getString("published_at"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                posts.add(post);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting posts: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return posts;
    }

    public List<Post> getPostsFromAuthor(int author_id) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Post> posts = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) return posts;

            String query = "SELECT p.*, u.username, u.full_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.author_id = u.id " +
                    "WHERE u.id = ? " +
                    "ORDER BY p.created_at DESC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, author_id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("author_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("post_type"),
                        rs.getBoolean("is_published"),
                        rs.getString("published_at"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                posts.add(post);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting posts: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return posts;
    }

    /**
     * Lấy thông tin chi tiết của một post
     */
    public Post getPostById(int postId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Post post = null;

        try {
            con = conSQL.conclass();
            if (con == null) return null;

            String query = "SELECT p.*, u.username, u.full_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.author_id = u.id " +
                    "WHERE p.id = ?";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, postId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                post = new Post(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("author_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("post_type"),
                        rs.getBoolean("is_published"),
                        rs.getString("published_at"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting post by id: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return post;
    }

    /**
     * Kiểm tra database có trống không
     */
    public boolean isDatabaseEmpty() {
        return getUsersCount() == 0;
    }

    /**
     * Lấy tất cả comments của một post
     */
    public List<Comment> getCommentsByPostId(int postId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Comment> comments = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) return comments;

            String query = "SELECT c.*, u.username, u.full_name, u.avatar " +
                    "FROM comments c " +
                    "LEFT JOIN users u ON c.commenter_id = u.id " +
                    "WHERE c.post_id = ? " +
                    "ORDER BY c.created_at ASC";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, postId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment(
                        rs.getInt("id"),
                        rs.getInt("post_id"),
                        rs.getInt("commenter_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("avatar"),
                        rs.getString("content"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                comments.add(comment);
            }
            Log.i(TAG, "Retrieved " + comments.size() + " comments for post " + postId);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting comments: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return comments;
    }

    /**
     * Thêm comment mới
     */
    public long addComment(int postId, int commenterId, String content) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long commentId = -1;

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null");
                return -1;
            }

            String query = "INSERT INTO comments (post_id, commenter_id, content) " +
                    "VALUES (?, ?, ?); SELECT SCOPE_IDENTITY() as id";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, postId);
            stmt.setInt(2, commenterId);
            stmt.setString(3, content);

            rs = stmt.executeQuery();
            if (rs.next()) {
                commentId = rs.getLong("id");
                Log.i(TAG, "Comment added successfully with ID: " + commentId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding comment: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return commentId;
    }

    public boolean addTestUsers() {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            // Kiểm tra xem đã có dữ liệu test chưa
            if (checkUsernameExists("admin")) {
                Log.i(TAG, "Test users already exist");
                return true;
            }

            // Tài khoản Admin
            String query = "INSERT INTO users (username, email, password, full_name, phone, class_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            // Admin account
            stmt = con.prepareStatement(query);
            stmt.setString(1, "admin");
            stmt.setString(2, "admin@studysupport.com");
            stmt.setString(3, "Admin@123");
            stmt.setString(4, "Quản trị viên hệ thống");
            stmt.setString(5, "0909123456");
            stmt.setInt(6, 1); // class_id
            stmt.executeUpdate();
            stmt.close();

            // Teacher account
            stmt = con.prepareStatement(query);
            stmt.setString(1, "teacher01");
            stmt.setString(2, "teacher01@gmail.com");
            stmt.setString(3, "Teacher@123");
            stmt.setString(4, "Nguyễn Văn A");
            stmt.setString(5, "0912345678");
            stmt.setInt(6, 1);
            stmt.executeUpdate();
            stmt.close();

            // Student 1 account
            stmt = con.prepareStatement(query);
            stmt.setString(1, "student01");
            stmt.setString(2, "student01@gmail.com");
            stmt.setString(3, "Student@123");
            stmt.setString(4, "Trần Thị B");
            stmt.setString(5, "0923456789");
            stmt.setInt(6, 1);
            stmt.executeUpdate();
            stmt.close();

            // Student 2 account
            stmt = con.prepareStatement(query);
            stmt.setString(1, "student02");
            stmt.setString(2, "student02@gmail.com");
            stmt.setString(3, "Password123");
            stmt.setString(4, "Lê Văn C");
            stmt.setString(5, "0934567890");
            stmt.setInt(6, 1);
            stmt.executeUpdate();

            success = true;
            Log.i(TAG, "Test users added successfully");
        } catch (SQLException e) {
            Log.e(TAG, "Error adding test users: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    // ---------------------- SCHEDULES TABLE METHODS ----------------------

    /**
     * Lấy tất cả các lịch của người dùng, sắp xếp theo ngày tăng dần
     */
    public List<Schedule> getSchedulesForUser(int userId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Schedule> schedules = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null");
                return schedules;
            }

            String query = "SELECT * FROM personal_schedules " +
                    "WHERE user_id = ? " +
                    "ORDER BY schedule_date ASC, start_time ASC";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("schedule_date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("schedule_type"),
                        rs.getString("created_at")
                );
                schedules.add(schedule);
            }
            Log.i(TAG, "Retrieved " + schedules.size() + " schedules for user " + userId);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting schedules for user: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return schedules;
    }

    /**
     * Thêm lịch mới cho người dùng
     */
    public long addSchedule(int userId, String title, String description, String scheduleDate,
                           String startTime, String endTime, String scheduleType) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long scheduleId = -1;

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null");
                return -1;
            }

            String query = "INSERT INTO personal_schedules (user_id, title, description, schedule_date, " +
                    "start_time, end_time, schedule_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY() as id";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, scheduleDate);
            stmt.setString(5, startTime);
            stmt.setString(6, endTime);
            stmt.setString(7, scheduleType != null ? scheduleType : "personal");

            rs = stmt.executeQuery();
            if (rs.next()) {
                scheduleId = rs.getLong("id");
                Log.i(TAG, "Schedule added successfully with ID: " + scheduleId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding schedule: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return scheduleId;
    }

    /**
     * Cập nhật lịch
     */
    public boolean updateSchedule(int scheduleId, String title, String description,
                                 String scheduleDate, String startTime, String endTime, String scheduleType) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "UPDATE personal_schedules SET title = ?, description = ?, " +
                    "schedule_date = ?, start_time = ?, end_time = ?, schedule_type = ? WHERE id = ?";

            stmt = con.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, scheduleDate);
            stmt.setString(4, startTime);
            stmt.setString(5, endTime);
            stmt.setString(6, scheduleType != null ? scheduleType : "personal");
            stmt.setInt(7, scheduleId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
            Log.i(TAG, "Schedule updated: " + success);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating schedule: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Xóa lịch
     */
    public boolean deleteSchedule(int scheduleId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "DELETE FROM personal_schedules WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, scheduleId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
            Log.i(TAG, "Schedule deleted: " + success);
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting schedule: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Lấy lịch theo ID
     */
    public Schedule getScheduleById(int scheduleId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Schedule schedule = null;

        try {
            con = conSQL.conclass();
            if (con == null) return null;

            String query = "SELECT * FROM personal_schedules WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, scheduleId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                schedule = new Schedule(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("schedule_date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("schedule_type"),
                        rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting schedule by id: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return schedule;
    }

    /**
     * Đóng tài nguyên database
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection con) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            Log.e(TAG, "Error closing resources: " + e.getMessage());
        }
    }
}