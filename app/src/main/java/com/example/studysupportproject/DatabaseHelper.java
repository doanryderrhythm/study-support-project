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

    /**
     * Safe connection getter with null check and logging
     */
    private Connection getConnection() {
        Connection con = conSQL.conclass();
        if (con == null) {
            Log.e(TAG, "Failed to obtain database connection");
        }
        return con;
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
            if (con == null) {
                Log.e(TAG, "Connection is null in checkLogin");
                return null;
            }

            String query = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";
            stmt = con.prepareStatement(query);
            if (stmt == null) {
                Log.e(TAG, "PreparedStatement is null in checkLogin");
                return null;
            }
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
                
                // Get role from role_id
                int roleId = rs.getInt("role_id");
                String role = getRoleNameById(roleId);
                user.setRole(role);
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
            if (con == null) {
                Log.e(TAG, "Connection is null in getUserByEmail");
                return null;
            }

            String query = "SELECT * FROM users WHERE email = ?";
            stmt = con.prepareStatement(query);
            if (stmt == null) {
                Log.e(TAG, "PreparedStatement is null in getUserByEmail");
                return null;
            }
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
            if (con == null) {
                Log.e(TAG, "Connection is null in getUserById");
                return null;
            }

            String query = "SELECT u.*, r.role_name FROM users u LEFT JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
            stmt = con.prepareStatement(query);
            if (stmt == null) {
                Log.e(TAG, "PreparedStatement is null in getUserById");
                return null;
            }
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
     * Get role name by role ID
     */
    public String getRoleNameById(int roleId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String roleName = "student"; // default role

        try {
            con = conSQL.conclass();
            if (con == null) return roleName;

            String query = "SELECT role_name FROM roles WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, roleId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                roleName = rs.getString("role_name");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting role name by id: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return roleName;
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
    public long createPost(String title, String content, int authorId, String postType, int privacyType) {
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

            String query = "{CALL CreatePost(?, ?, ?, ?, ?)}";
            stmt = con.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, authorId);
            stmt.setString(4, postType);
            stmt.setInt(5, privacyType);

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
    public boolean updatePost(int postId, String title, String content, String postType, int privacyType) {
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

            String query = "{CALL UpdatePost(?, ?, ?, ?, ?)}";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, postId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.setString(4, postType);
            stmt.setInt(5, privacyType);

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
                        "WHERE p.is_published = 1 " +
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
     * Cập nhật avatar của user
     */
    public boolean updateUserAvatar(int userId, String avatarUrl) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "UPDATE users SET avatar = ?, updated_at = GETDATE() WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, avatarUrl);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;

            if (success) {
                Log.i(TAG, "Avatar updated successfully for user: " + userId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating avatar: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
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
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in getSchedulesForUser");
                return schedules;
            }

            String query = "SELECT * FROM personal_schedules " +
                    "WHERE user_id = ? " +
                    "ORDER BY schedule_date ASC, start_time ASC";

            stmt = con.prepareStatement(query);
            if (stmt == null) {
                Log.e(TAG, "PreparedStatement is null in getSchedulesForUser");
                return schedules;
            }
            
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
            Log.e(TAG, "SQL Error getting schedules for user: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in getSchedulesForUser: " + e.getMessage(), e);
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

    // ---------------------- SCHOOLS TABLE METHODS ----------------------

    /**
     * Lấy tất cả các trường học
     */
    public List<School> getAllSchools() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<School> schools = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null in getAllSchools");
                return schools;
            }

            String query = "SELECT * FROM schools ORDER BY school_name ASC";
            stmt = con.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                School school = new School(
                        rs.getInt("id"),
                        rs.getString("school_name")
                );
                schools.add(school);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting all schools: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return schools;
    }

    /**
     * Thêm trường học mới
     */
    public long addSchool(String schoolName) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long schoolId = -1;

        try {
            con = conSQL.conclass();
            if (con == null) {
                Log.e(TAG, "Connection is null in addSchool");
                return -1;
            }

            String query = "INSERT INTO schools (school_name) " +
                    "VALUES (?); SELECT SCOPE_IDENTITY() as id";

            stmt = con.prepareStatement(query);
            stmt.setString(1, schoolName);

            rs = stmt.executeQuery();
            if (rs.next()) {
                schoolId = rs.getLong("id");
                Log.i(TAG, "School added with ID: " + schoolId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding school: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return schoolId;
    }

    /**
     * Cập nhật trường học
     */
    public boolean updateSchool(int schoolId, String schoolName) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "UPDATE schools SET school_name = ? WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, schoolName);
            stmt.setInt(2, schoolId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating school: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Lấy trường học theo ID
     */
    public School getSchoolById(int schoolId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        School school = null;

        try {
            con = conSQL.conclass();
            if (con == null) return null;

            String query = "SELECT * FROM schools WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, schoolId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                school = new School(
                        rs.getInt("id"),
                        rs.getString("school_name")
                );
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting school by id: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return school;
    }

    // ---------------------- SEMESTERS TABLE METHODS ----------------------

    /**
     * Lấy các học kỳ theo trường học
     */
    public List<Semester> getSemestersBySchool(int schoolId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Semester> semesters = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) return semesters;

            String query = "SELECT s.id, s.semester_name FROM semesters s " +
                    "INNER JOIN school_semesters ss ON s.id = ss.semester_id " +
                    "WHERE ss.school_id = ? ORDER BY s.id DESC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, schoolId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Semester semester = new Semester(
                        rs.getInt("id"),
                        rs.getString("semester_name")
                );
                semesters.add(semester);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting semesters by school: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return semesters;
    }

    /**
     * Thêm học kỳ mới
     */
    public long addSemester(int schoolId, String semesterName) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long semesterId = -1;

        try {
            con = conSQL.conclass();
            if (con == null) return -1;

            String query = "INSERT INTO semesters (semester_name) VALUES (?); SELECT SCOPE_IDENTITY() as id";
            stmt = con.prepareStatement(query);
            stmt.setString(1, semesterName);

            rs = stmt.executeQuery();
            if (rs.next()) {
                semesterId = rs.getLong("id");
                Log.i(TAG, "Semester added with ID: " + semesterId);

                String linkQuery = "INSERT INTO school_semesters (school_id, semester_id) VALUES (?, ?)";
                PreparedStatement linkStmt = con.prepareStatement(linkQuery);
                linkStmt.setInt(1, schoolId);
                linkStmt.setLong(2, semesterId);
                linkStmt.executeUpdate();
                linkStmt.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding semester: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return semesterId;
    }

    /**
     * Cập nhật học kỳ
     */
    public boolean updateSemester(int semesterId, String semesterName) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "UPDATE semesters SET semester_name = ? WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, semesterName);
            stmt.setInt(2, semesterId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating semester: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Lấy học kỳ theo ID
     */
    public Semester getSemesterById(int semesterId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Semester semester = null;

        try {
            con = conSQL.conclass();
            if (con == null) return null;

            String query = "SELECT * FROM semesters WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, semesterId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                semester = new Semester(
                        rs.getInt("id"),
                        rs.getString("semester_name")
                );
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting semester by id: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return semester;
    }

    // ---------------------- CLASSES TABLE METHODS ----------------------

    /**
     * Lấy các lớp theo học kỳ
     */
    public List<ClassItem> getClassesBySemester(int semesterId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ClassItem> classes = new ArrayList<>();

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in getClassesBySemester");
                return classes;
            }

            String query = "SELECT c.id, c.school_id, c.semester_id, c.class_name, " +
                    "s.id as subject_id, s.subject_name " +
                    "FROM classes c " +
                    "LEFT JOIN class_subjects cs ON c.id = cs.class_id " +
                    "LEFT JOIN subjects s ON cs.subject_id = s.id " +
                    "WHERE c.semester_id = ? " +
                    "ORDER BY c.class_name ASC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, semesterId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ClassItem classItem = new ClassItem(
                        rs.getInt("id"),
                        rs.getInt("school_id"),
                        rs.getInt("semester_id"),
                        rs.getString("class_name"),
                        rs.getInt("subject_id") > 0 ? rs.getInt("subject_id") : -1,
                        rs.getString("subject_name")
                );
                classes.add(classItem);
            }
            Log.i(TAG, "Retrieved " + classes.size() + " classes for semester " + semesterId);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting classes by semester: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return classes;
    }

    /**
     * Thêm lớp mới
     */
    public long addClass(String className, int semesterId, int schoolId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long classId = -1;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in addClass");
                return -1;
            }

            String query = "INSERT INTO classes (school_id, semester_id, class_name) " +
                    "VALUES (?, ?, ?); SELECT SCOPE_IDENTITY() as id";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, schoolId);
            stmt.setInt(2, semesterId);
            stmt.setString(3, className);

            rs = stmt.executeQuery();
            if (rs.next()) {
                classId = rs.getLong("id");
                Log.i(TAG, "Class added with ID: " + classId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding class: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return classId;
    }

    /**
     * Cập nhật lớp
     */
    public boolean updateClass(int classId, String className) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in updateClass");
                return false;
            }

            String query = "UPDATE classes SET class_name = ? WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, className);
            stmt.setInt(2, classId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
            
            if (success) {
                Log.i(TAG, "Class " + classId + " updated successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating class: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Xóa lớp
     */
    public boolean deleteClass(int classId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in deleteClass");
                return false;
            }

            String query = "DELETE FROM classes WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
            
            if (success) {
                Log.i(TAG, "Class " + classId + " deleted successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting class: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Delete a semester by ID (cascades to delete all classes and their relationships)
     */
    /**
     * Check if semester has any classes attached
     */
    public boolean hasClassesInSemester(int semesterId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in hasClassesInSemester");
                return false;
            }

            String query = "SELECT COUNT(*) as count FROM classes WHERE semester_id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, semesterId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking classes in semester: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }

        return count > 0;
    }

    public boolean deleteSemester(int semesterId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in deleteSemester");
                return false;
            }

            // First, get all classes in this semester
            String getClassesQuery = "SELECT id FROM classes WHERE semester_id = ?";
            stmt = con.prepareStatement(getClassesQuery);
            stmt.setInt(1, semesterId);
            rs = stmt.executeQuery();

            List<Integer> classIds = new ArrayList<>();
            while (rs.next()) {
                classIds.add(rs.getInt("id"));
            }

            // Close resources for this query
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();

            // Delete all relationships for each class
            for (int classId : classIds) {
                // Delete class_teachers
                String deleteTeachersQuery = "DELETE FROM class_teachers WHERE class_id = ?";
                stmt = con.prepareStatement(deleteTeachersQuery);
                stmt.setInt(1, classId);
                stmt.executeUpdate();
                stmt.close();

                // Delete class_subjects
                String deleteSubjectsQuery = "DELETE FROM class_subjects WHERE class_id = ?";
                stmt = con.prepareStatement(deleteSubjectsQuery);
                stmt.setInt(1, classId);
                stmt.executeUpdate();
                stmt.close();

                // Delete students in class (if such relationship exists)
                String deleteStudentsQuery = "DELETE FROM class_students WHERE class_id = ?";
                stmt = con.prepareStatement(deleteStudentsQuery);
                stmt.setInt(1, classId);
                stmt.executeUpdate();
                stmt.close();
            }

            // Delete all classes in this semester
            String deleteClassesQuery = "DELETE FROM classes WHERE semester_id = ?";
            stmt = con.prepareStatement(deleteClassesQuery);
            stmt.setInt(1, semesterId);
            stmt.executeUpdate();
            stmt.close();

            // Finally, delete the semester
            String deleteSemesterQuery = "DELETE FROM semesters WHERE id = ?";
            stmt = con.prepareStatement(deleteSemesterQuery);
            stmt.setInt(1, semesterId);
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;

            if (success) {
                Log.i(TAG, "Semester " + semesterId + " and all its classes deleted successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting semester: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return success;
    }

    /**
     * Lấy lớp theo ID
     */
    public ClassItem getClassById(int classId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ClassItem classItem = null;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in getClassById");
                return null;
            }

            String query = "SELECT c.id, c.school_id, c.semester_id, c.class_name, " +
                    "s.id as subject_id, s.subject_name " +
                    "FROM classes c " +
                    "LEFT JOIN class_subjects cs ON c.id = cs.class_id " +
                    "LEFT JOIN subjects s ON cs.subject_id = s.id " +
                    "WHERE c.id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                classItem = new ClassItem(
                        rs.getInt("id"),
                        rs.getInt("school_id"),
                        rs.getInt("semester_id"),
                        rs.getString("class_name"),
                        rs.getInt("subject_id") > 0 ? rs.getInt("subject_id") : -1,
                        rs.getString("subject_name")
                );
                Log.i(TAG, "Class " + classId + " retrieved successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting class by id: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return classItem;
    }

    /**
     * Lấy học sinh trong lớp
     */
    public List<User> getStudentsByClass(int classId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> students = new ArrayList<>();

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in getStudentsByClass");
                return students;
            }

            String query = "SELECT u.* FROM users u INNER JOIN class_students cs ON u.id = cs.student_id WHERE cs.class_id = ? ORDER BY u.full_name ASC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User student = new User();
                student.setId(rs.getInt("id"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                student.setFullName(rs.getString("full_name"));
                student.setPhone(rs.getString("phone"));
                student.setAvatar(rs.getString("avatar"));
                students.add(student);
            }
            Log.i(TAG, "Retrieved " + students.size() + " students for class " + classId);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting students by class: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return students;
    }

    /**
     * Lấy học sinh chưa được thêm vào lớp
     */
    public List<User> getStudentsNotInClass(int classId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> students = new ArrayList<>();

        try {
            con = getConnection();
            if (con == null) return students;

            String query = "SELECT u.* FROM users u " +
                    "WHERE u.role_id IN (SELECT id FROM roles WHERE role_name = 'student') " +
                    "AND u.id NOT IN (SELECT student_id FROM class_students WHERE class_id = ?) " +
                    "ORDER BY u.full_name ASC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User student = new User();
                student.setId(rs.getInt("id"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                student.setFullName(rs.getString("full_name"));
                student.setPhone(rs.getString("phone"));
                student.setAvatar(rs.getString("avatar"));
                students.add(student);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting students not in class: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return students;
    }

    /**
     * Thêm học sinh vào lớp
     */
    public boolean addStudentToClass(int classId, int studentId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in addStudentToClass");
                return false;
            }

            String query = "INSERT INTO class_students (class_id, student_id) VALUES (?, ?)";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
            
            if (success) {
                Log.i(TAG, "Student " + studentId + " added to class " + classId);
            } else {
                Log.w(TAG, "Failed to add student " + studentId + " to class " + classId + ": 0 rows affected");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding student " + studentId + " to class " + classId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Remove student from class (only delete relationship, not the user)
     */
    public boolean removeStudentFromClass(int classId, int studentId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "DELETE FROM class_students WHERE class_id = ? AND student_id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error removing student from class: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Get teachers for a specific class
     */
    public List<User> getTeachersByClass(int classId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> teachers = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) return teachers;

            String query = "SELECT u.id, u.username, u.email, u.full_name, u.phone FROM users u " +
                    "INNER JOIN class_teachers ct ON u.id = ct.teacher_id " +
                    "WHERE ct.class_id = ? ORDER BY u.full_name ASC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User teacher = new User();
                teacher.setId(rs.getInt("id"));
                teacher.setUsername(rs.getString("username"));
                teacher.setEmail(rs.getString("email"));
                teacher.setFullName(rs.getString("full_name"));
                teacher.setPhone(rs.getString("phone"));
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting teachers by class: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return teachers;
    }

    /**
     * Get teachers not in a specific class
     */
    public List<User> getTeachersNotInClass(int classId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> teachers = new ArrayList<>();

        try {
            con = conSQL.conclass();
            if (con == null) return teachers;

            String query = "SELECT u.id, u.username, u.email, u.full_name, u.phone FROM users u " +
                    "WHERE u.role_id IN (SELECT id FROM roles WHERE role_name = 'teacher') " +
                    "AND u.id NOT IN (SELECT teacher_id FROM class_teachers WHERE class_id = ?) " +
                    "ORDER BY u.full_name ASC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User teacher = new User();
                teacher.setId(rs.getInt("id"));
                teacher.setUsername(rs.getString("username"));
                teacher.setEmail(rs.getString("email"));
                teacher.setFullName(rs.getString("full_name"));
                teacher.setPhone(rs.getString("phone"));
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting teachers not in class: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return teachers;
    }

    /**
     * Add teacher to class
     */
    public boolean addTeacherToClass(int classId, int teacherId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "INSERT INTO class_teachers (class_id, teacher_id) VALUES (?, ?)";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            stmt.setInt(2, teacherId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error adding teacher to class: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Remove teacher from class
     */
    public boolean removeTeacherFromClass(int classId, int teacherId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = conSQL.conclass();
            if (con == null) return false;

            String query = "DELETE FROM class_teachers WHERE class_id = ? AND teacher_id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            stmt.setInt(2, teacherId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error removing teacher from class: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    // ---------------------- SUBJECTS TABLE METHODS ----------------------

    /**
     * Get all subjects from the database
     */
    public List<Subject> getAllSubjects() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Subject> subjects = new ArrayList<>();

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in getAllSubjects");
                return subjects;
            }

            String query = "SELECT id, subject_name FROM subjects ORDER BY subject_name ASC";
            stmt = con.prepareStatement(query);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getInt("id"),
                        rs.getString("subject_name")
                );
                subjects.add(subject);
            }
            Log.i(TAG, "Retrieved " + subjects.size() + " subjects");
        } catch (SQLException e) {
            Log.e(TAG, "Error getting all subjects: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return subjects;
    }

    /**
     * Update class subject via class_subjects junction table
     */
    public boolean updateClassSubject(int classId, int subjectId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in updateClassSubject");
                return false;
            }

            // First, delete existing subject relationships for this class
            String deleteQuery = "DELETE FROM class_subjects WHERE class_id = ?";
            stmt = con.prepareStatement(deleteQuery);
            stmt.setInt(1, classId);
            stmt.executeUpdate();
            stmt.close();

            // Then, insert the new subject relationship if subjectId is valid
            if (subjectId > 0) {
                String insertQuery = "INSERT INTO class_subjects (class_id, subject_id) VALUES (?, ?)";
                stmt = con.prepareStatement(insertQuery);
                stmt.setInt(1, classId);
                stmt.setInt(2, subjectId);
                int rowsAffected = stmt.executeUpdate();
                success = rowsAffected > 0;
            } else {
                success = true; // No subject selected is valid
            }
            
            if (success) {
                Log.i(TAG, "Class " + classId + " subject updated to " + subjectId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating class subject: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Get subjects for a specific class
     */
    public List<Subject> getSubjectsByClass(int classId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Subject> subjects = new ArrayList<>();

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in getSubjectsByClass");
                return subjects;
            }

            String query = "SELECT s.id, s.subject_name FROM subjects s " +
                    "INNER JOIN class_subjects cs ON s.id = cs.subject_id " +
                    "WHERE cs.class_id = ? " +
                    "ORDER BY s.subject_name ASC";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getInt("id"),
                        rs.getString("subject_name")
                );
                subjects.add(subject);
            }
            Log.i(TAG, "Retrieved " + subjects.size() + " subjects for class " + classId);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting subjects by class: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return subjects;
    }

    /**
     * Get all semesters assigned to a teacher
     */
    public List<Semester> getTeacherSemesters(int teacherId) {
        List<Semester> semesters = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            String query = "SELECT DISTINCT s.id, s.semester_name " +
                    "FROM semesters s " +
                    "INNER JOIN classes c ON s.id = c.semester_id " +
                    "INNER JOIN class_teachers ct ON c.id = ct.class_id " +
                    "WHERE ct.teacher_id = ? " +
                    "ORDER BY s.semester_name";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, teacherId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Semester semester = new Semester(
                        rs.getInt("id"),
                        rs.getString("semester_name")
                );
                semesters.add(semester);
            }
            Log.i(TAG, "Retrieved " + semesters.size() + " semesters for teacher " + teacherId);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting teacher semesters: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return semesters;
    }

    /**
     * Get all classes assigned to a teacher in a specific semester
     */
    public List<ClassItem> getTeacherClassesBySemester(int semesterId, int teacherId) {
        List<ClassItem> classes = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            String query = "SELECT DISTINCT c.id, c.school_id, c.semester_id, c.class_name, " +
                    "s.id as subject_id, s.subject_name " +
                    "FROM classes c " +
                    "INNER JOIN class_teachers ct ON c.id = ct.class_id " +
                    "LEFT JOIN class_subjects cs ON c.id = cs.class_id " +
                    "LEFT JOIN subjects s ON cs.subject_id = s.id " +
                    "WHERE c.semester_id = ? AND ct.teacher_id = ? " +
                    "ORDER BY c.class_name";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, semesterId);
            stmt.setInt(2, teacherId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ClassItem classItem = new ClassItem(
                        rs.getInt("id"),
                        rs.getInt("school_id"),
                        rs.getInt("semester_id"),
                        rs.getString("class_name"),
                        rs.getInt("subject_id") > 0 ? rs.getInt("subject_id") : 0,
                        rs.getString("subject_name")
                );
                classes.add(classItem);
            }
            Log.i(TAG, "Retrieved " + classes.size() + " classes for teacher " + teacherId + " in semester " + semesterId);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting teacher classes by semester: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return classes;
    }

    /**
     * Add a new subject
     */
    public boolean addSubject(String subjectName, int schoolId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in addSubject");
                return false;
            }

            String query = "INSERT INTO subjects (subject_name, school_id) VALUES (?, ?)";
            stmt = con.prepareStatement(query);
            stmt.setString(1, subjectName);
            stmt.setInt(2, schoolId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;

            if (success) {
                Log.i(TAG, "Subject '" + subjectName + "' added successfully for school " + schoolId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding subject: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Delete a subject by ID
     */
    public boolean deleteSubject(int subjectId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in deleteSubject");
                return false;
            }

            String query = "DELETE FROM subjects WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, subjectId);

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;

            if (success) {
                Log.i(TAG, "Subject " + subjectId + " deleted successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting subject: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    public boolean updateSubject(Subject subject) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in updateSubject");
                return false;
            }

            String query = "UPDATE subjects SET subject_name = ? WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, subject.getName());
            stmt.setInt(2, subject.getId());

            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;

            if (success) {
                Log.i(TAG, "Subject " + subject.getId() + " updated successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating subject: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }

    /**
     * Check if a subject has any classes attached to it
     */
    public boolean hasClassesAttached(int subjectId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean hasClasses = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in hasClassesAttached");
                return false;
            }

            String query = "SELECT COUNT(*) as count FROM class_subjects WHERE subject_id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, subjectId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                hasClasses = count > 0;
            }

            Log.i(TAG, "Subject " + subjectId + " has " + (hasClasses ? "attached classes" : "no attached classes"));
        } catch (SQLException e) {
            Log.e(TAG, "Error checking attached classes: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return hasClasses;
    }

    // ---------------------- SCHOOLS TABLE METHODS ----------------------

    /**
     * Check if a school has any semesters attached to it
     */
    public boolean hasSemestersAttached(int schoolId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean hasSemesters = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in hasSemestersAttached");
                return false;
            }

            String query = "SELECT COUNT(*) as count FROM school_semesters WHERE school_id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, schoolId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                hasSemesters = count > 0;
            }

            Log.i(TAG, "School " + schoolId + " has " + (hasSemesters ? "attached semesters" : "no attached semesters"));
        } catch (SQLException e) {
            Log.e(TAG, "Error checking attached semesters: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, con);
        }
        return hasSemesters;
    }

    /**
     * Delete a school by ID
     */
    public boolean deleteSchool(int schoolId) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            con = getConnection();
            if (con == null) {
                Log.e(TAG, "Connection is null in deleteSchool");
                return false;
            }

            String query = "DELETE FROM schools WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, schoolId);
            int rowsAffected = stmt.executeUpdate();

            success = rowsAffected > 0;
            Log.i(TAG, "School " + schoolId + " deleted successfully. Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting school: " + e.getMessage());
        } finally {
            closeResources(null, stmt, con);
        }
        return success;
    }
}
