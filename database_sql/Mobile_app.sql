CREATE DATABASE Mobile_app;
USE Mobile_app;
GO



-- Bảng trường
CREATE TABLE schools (
    id INT IDENTITY(1,1) PRIMARY KEY,
    school_name NVARCHAR(50) UNIQUE NOT NULL
)

-- Bảng lớp
CREATE TABLE classes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    school_id INT NOT NULL,
    class_name NVARCHAR(50) NOT NULL,
    FOREIGN KEY (school_id) REFERENCES schools(id)
)

-- Bảng học kỳ
CREATE TABLE semesters (
    id INT IDENTITY(1,1) PRIMARY KEY,
    semester_name NVARCHAR(50) NOT NULL
)

-- Bảng vai trò
CREATE TABLE roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    role_name NVARCHAR(50) UNIQUE NOT NULL,
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100),
    full_name NVARCHAR(100),
    date_of_birth DATE,
    class_id INT NOT NULL,
    role_id INT NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(255),
    avatar NVARCHAR(255),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Bảng phân quyền user
CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    granted_at DATETIME2 DEFAULT GETDATE(),
    granted_by INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (granted_by) REFERENCES users(id)
);

-- Bảng quyền
CREATE TABLE permissions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    permission_name NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE()
);

-- Bảng gán quyền cho role
CREATE TABLE role_permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Bảng bài viết
CREATE TABLE posts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    content NTEXT,
    author_id INT NOT NULL,
    post_type NVARCHAR(50) DEFAULT N'general', -- general, announcement, grade, etc.
    is_published BIT DEFAULT 0,
    published_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- Bảng điểm 
CREATE TABLE grades (
    id INT IDENTITY(1,1) PRIMARY KEY,
    student_id INT NOT NULL,
    class_id INT NOT NULL,
    subject_name NVARCHAR(100) NOT NULL,
    grade_value DECIMAL(5,2) NOT NULL,
    grade_type NVARCHAR(50) DEFAULT N'midterm', 
    semester_id INT NOT NULL,
    school_year NVARCHAR(20),
    teacher_id INT,
    notes NTEXT,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id)
);

-- Bảng lịch cá nhân 
CREATE TABLE personal_schedules (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NTEXT,
    schedule_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    schedule_type NVARCHAR(50) DEFAULT N'personal', 
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
);

-- Bảng cài đặt mạng xã hội 
CREATE TABLE social_settings (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    setting_key NVARCHAR(100) NOT NULL,
    setting_value NVARCHAR(255),
    setting_type NVARCHAR(50) DEFAULT N'privacy', 
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bảng lịch sử đăng nhập 
CREATE TABLE login_history (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    login_time DATETIME2 DEFAULT GETDATE(),
    ip_address NVARCHAR(45),
    user_agent NVARCHAR(255),
    login_status NVARCHAR(20) DEFAULT N'success',
    failure_reason NVARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bảng khôi phục mật khẩu 
CREATE TABLE password_resets (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    reset_token NVARCHAR(255) NOT NULL,
    expires_at DATETIME2 NOT NULL,
    used BIT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Thêm các vai trò
INSERT INTO roles (role_name, description) VALUES
(N'admin', N'Quản trị hệ thống - Toàn quyền'),
(N'teacher', N'Giáo viên - Quản lý lớp học và điểm số'),
(N'student', N'Học sinh - Xem điểm và bài viết');

-- Thêm các quyền chi tiết theo ảnh
INSERT INTO permissions (permission_name, description) VALUES

-- Quản lý người dùng & đăng nhập
(N'user_management', N'Quản lý người dùng'),
(N'login', N'Đăng nhập hệ thống'),
(N'register', N'Đăng ký tài khoản'),

-- Quản lý bài viết
(N'view_posts', N'Xem bài viết'),
(N'create_posts', N'Đăng bài viết'),
(N'edit_posts', N'Sửa bài viết'),
(N'delete_posts', N'Xóa bài viết'),
(N'publish_posts', N'Xuất bản bài viết'),

-- Quản lý điểm
(N'view_grades', N'Xem điểm'),
(N'manage_grades', N'Quản lý điểm'),
(N'export_grades', N'Xuất bảng điểm'),

-- Lịch và lịch trình
(N'view_schedule', N'Xem lịch'),
(N'manage_schedule', N'Quản lý lịch'),
(N'view_personal_calendar', N'Xem lịch cá nhân'),

-- Cài đặt
(N'system_settings', N'Cài đặt hệ thống'),
(N'social_settings', N'Cài đặt mạng xã hội'),
(N'privacy_settings', N'Cài đặt bảo mật'),

-- Bảo mật
(N'password_reset', N'Đặt lại mật khẩu'),
(N'account_security', N'Bảo mật tài khoản'),
(N'view_login_history', N'Xem lịch sử đăng nhập');

-- Gán quyền cho admin
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_name = N'admin';

-- Gán quyền cho teacher
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_name = N'teacher'
AND p.permission_name IN (N'view_posts', N'create_posts', N'edit_posts', N'publish_posts', 
                         N'view_grades', N'manage_grades', N'export_grades',
                         N'view_schedule', N'manage_schedule');

-- Gán quyền cho student
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_name = N'student'
AND p.permission_name IN (N'view_posts', N'view_grades', N'view_schedule', 
                         N'view_personal_calendar', N'password_reset', N'account_security');

-- Tạo trường
INSERT INTO schools (school_name)
VALUES (N'Trường Đại học UIT')

-- Tạo lớp
INSERT INTO classes (class_name, school_id)
VALUES (N'SE114', '1')

-- Tạo tài khoản admin
INSERT INTO users (username, password, email, full_name, class_id, role_id) 
VALUES (N'Danh', N'danh123', N'danh123@school.edu.vn', N'Quản Trị Viên', '1', '1');

-- Gán quyền admin
INSERT INTO user_roles (user_id, role_id, granted_by) 
VALUES (1, 1, 1);
GO

-- Stored Procedure đăng bài viết
CREATE PROCEDURE CreatePost
    @title NVARCHAR(255),
    @content NTEXT,
    @author_id INT,
    @post_type NVARCHAR(50) = N'general'
AS
BEGIN
    INSERT INTO posts (title, content, author_id, post_type, is_published, published_at)
    VALUES (@title, @content, @author_id, @post_type, 1, GETDATE());
    
    SELECT SCOPE_IDENTITY() as post_id;
END;
GO

-- Stored Procedure nhập điểm
CREATE PROCEDURE AddGrade
    @student_id INT,
    @class_id INT,
    @subject_name NVARCHAR(100),
    @grade_value DECIMAL(5,2),
    @grade_type NVARCHAR(50),
    @semester_id INT,
    @school_year NVARCHAR(20),
    @teacher_id INT
AS
BEGIN
    INSERT INTO grades (student_id, class_id, subject_name, grade_value, grade_type, semester_id, school_year, teacher_id)
    VALUES (@student_id, @class_id, @subject_name, @grade_value, @grade_type, @semester_id, @school_year, @teacher_id);
    
    SELECT SCOPE_IDENTITY() as grade_id;
END;
GO

-- Stored Procedure xem bảng điểm học sinh
CREATE PROCEDURE GetStudentGrades
    @student_id INT,
    @semester_id INT = NULL,
    @school_year NVARCHAR(20) = NULL
AS
BEGIN
    SELECT 
        g.subject_name,
        g.class_id,
        g.grade_value,
        g.grade_type,
        g.semester_id,
        g.school_year,
        g.created_at,
        t.full_name as teacher_name
    FROM grades g
    LEFT JOIN users t ON g.teacher_id = t.id
    WHERE g.student_id = @student_id
    AND (@semester_id IS NULL OR g.semester_id = @semester_id)
    AND (@school_year IS NULL OR g.school_year = @school_year)
    ORDER BY g.subject_name, g.grade_type;
END;
GO

-- Stored Procedure lấy lịch cá nhân
CREATE PROCEDURE GetPersonalSchedule
    @user_id INT,
    @start_date DATE,
    @end_date DATE
AS
BEGIN
    SELECT 
        id,
        title,
        description,
        schedule_date,
        start_time,
        end_time,
        schedule_type
    FROM personal_schedules
    WHERE user_id = @user_id
    AND schedule_date BETWEEN @start_date AND @end_date
    ORDER BY schedule_date, start_time;
END;
GO

-- Stored Procedure đặt lại mật khẩu
CREATE PROCEDURE ResetPassword
    @username NVARCHAR(50),
    @new_password NVARCHAR(255)
AS
BEGIN
    UPDATE users 
    SET password = @new_password, 
        updated_at = GETDATE()
    WHERE username = @username;
    
    IF @@ROWCOUNT > 0
        PRINT N'Đặt lại mật khẩu thành công!';
    ELSE
        PRINT N'Không tìm thấy người dùng!';
END;
GO