CREATE DATABASE Mobile_app;
USE Mobile_app;
GO

-- Bảng trường
CREATE TABLE schools (
    id INT IDENTITY(1,1) PRIMARY KEY,
    school_name NVARCHAR(50) UNIQUE NOT NULL
)

-- Bảng học kỳ
CREATE TABLE semesters (
    id INT IDENTITY(1,1) PRIMARY KEY,
    semester_name NVARCHAR(50) NOT NULL
);

-- Bảng liên kết trường học với học kỳ
CREATE TABLE school_semesters (
    school_id INT NOT NULL,
    semester_id INT NOT NULL,
    PRIMARY KEY (school_id, semester_id),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
)

-- Bảng môn học
CREATE TABLE subjects (
    id INT IDENTITY(1,1) PRIMARY KEY,
    subject_name NVARCHAR(100) NOT NULL,
    school_id INT NOT NULL,
    description NVARCHAR(255),
    FOREIGN KEY (school_id) REFERENCES schools(id)
);



-- Bảng vai trò
CREATE TABLE roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    role_name NVARCHAR(50) UNIQUE NOT NULL,
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE()
);

-- Create users table first (before classes since classes references users)
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100),
    full_name NVARCHAR(100),
    date_of_birth DATE,
    role_id INT NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(255),
    avatar NVARCHAR(255),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Bảng lớp
CREATE TABLE classes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    school_id INT NOT NULL,
    semester_id INT NOT NULL,
    class_name NVARCHAR(50) NOT NULL,
    FOREIGN KEY (school_id) REFERENCES schools(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id)
);

CREATE TABLE class_teachers (
    class_id INT NOT NULL,
    teacher_id INT NOT NULL,
    PRIMARY KEY (class_id, teacher_id),
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE class_students (
    class_id INT NOT NULL,
    student_id INT NOT NULL,
    PRIMARY KEY (class_id, student_id),
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE class_subjects (
    class_id INT NOT NULL,
    subject_id INT NOT NULL,
    PRIMARY KEY (class_id),
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);
-- Note: Each class is assigned exactly ONE subject (1:1 from class perspective, enforced by PRIMARY KEY on class_id)
-- One subject can be assigned to multiple classes (1:N relationship from subject perspective)

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

CREATE TABLE comments (
    id INT IDENTITY(1,1) PRIMARY KEY,
    post_id INT NOT NULL,
    commenter_id INT NOT NULL,
    content NTEXT,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (commenter_id) REFERENCES users(id)
)

-- Bảng điểm 
CREATE TABLE grades (
    id INT IDENTITY(1,1) PRIMARY KEY,
    student_id INT NOT NULL,
    class_id INT NOT NULL,
    grade_value DECIMAL(5,2) NOT NULL,
    grade_type NVARCHAR(50) NOT NULL CHECK (grade_type IN (N'quá trình', N'giữa kỳ', N'cuối kỳ', N'thực hành')),
    notes NTEXT,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (class_id) REFERENCES classes(id)
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
    FOREIGN KEY (user_id) REFERENCES users(id)
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


GO
-- Stored Procedure đăng bài viết
CREATE PROCEDURE CreatePost
    @title NVARCHAR(255),
    @content NTEXT,
    @author_id INT,
    @post_type NVARCHAR(50) = N'general',
    @privacy_type INT
AS
BEGIN
    INSERT INTO posts (title, content, author_id, post_type, is_published, published_at)
    VALUES (@title, @content, @author_id, @post_type, @privacy_type, GETDATE());
    
    SELECT SCOPE_IDENTITY() as post_id;
END;
GO

-- Stored Procedure sửa bài viết
CREATE PROCEDURE UpdatePost
    @post_id INT,
    @title NVARCHAR(255),
    @content NTEXT,
    @post_type NVARCHAR(50) = NULL,
    @privacy_type INT
AS
BEGIN
    -- Check if post exists
    IF NOT EXISTS (SELECT 1 FROM posts WHERE id = @post_id)
    BEGIN
        SELECT -1 as result;
        RETURN;
    END

    IF @post_type IS NOT NULL
    BEGIN
        UPDATE posts 
        SET title = @title,
            content = @content,
            post_type = @post_type,
            is_published = @privacy_type,
            updated_at = GETDATE()
        WHERE id = @post_id;
    END
    ELSE
    BEGIN
        UPDATE posts 
        SET title = @title,
            content = @content,
            updated_at = GETDATE()
        WHERE id = @post_id;
    END

    IF @@ROWCOUNT > 0
    BEGIN
        SELECT 1 as result;
    END
    ELSE
    BEGIN
        SELECT 0 as result;
    END
END;
GO

-- Stored Procedure nhập điểm (updated to match new schema without subject_name and school_year)
CREATE PROCEDURE AddGrade
    @student_id INT,
    @class_id INT,
    @grade_value DECIMAL(5,2),
    @grade_type NVARCHAR(50),
    @notes NTEXT = NULL
AS
BEGIN
    -- Validate student is enrolled in class
    IF NOT EXISTS (SELECT 1 FROM class_students WHERE class_id = @class_id AND student_id = @student_id)
    BEGIN
        SELECT -1 as grade_id; -- Error: student not in class
        RETURN;
    END
    
    INSERT INTO grades (student_id, class_id, grade_value, grade_type, notes)
    VALUES (@student_id, @class_id, @grade_value, @grade_type, @notes);
    
    SELECT SCOPE_IDENTITY() as grade_id;
END;
GO

-- Stored Procedure xem bảng điểm học sinh (updated to match new schema)
CREATE PROCEDURE GetStudentGrades
    @student_id INT,
    @semester_id INT = NULL
AS
BEGIN
    SELECT 
        g.id,
        g.class_id,
        g.grade_value,
        g.grade_type,
        g.notes,
        c.semester_id,
        c.class_name,
        c.school_id,
        g.created_at,
        g.updated_at,
        u.full_name as teacher_name
    FROM grades g
    INNER JOIN classes c ON g.class_id = c.id
    LEFT JOIN class_teachers ct ON c.id = ct.class_id
    LEFT JOIN users u ON ct.teacher_id = u.id
    WHERE g.student_id = @student_id
    AND (@semester_id IS NULL OR c.semester_id = @semester_id)
    ORDER BY c.class_name, g.grade_type;
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