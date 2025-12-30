-- Sample Data for Mobile_app Database
USE Mobile_app;
GO

--delete all data
EXEC sys.sp_msforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'
EXEC sys.sp_msforeachtable 'DELETE FROM ?'
EXEC sys.sp_MSForEachTable 'ALTER TABLE ? CHECK CONSTRAINT ALL'
EXEC sys.sp_MSForEachTable 'DECLARE @identValue BIGINT; IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID(''?'')) BEGIN SET @identValue = IDENT_CURRENT(''?''); IF @identValue > 0 DBCC CHECKIDENT (''?'', RESEED, 0); ELSE DBCC CHECKIDENT (''?'', RESEED, 1); END'

-- INSERT ORDER (respecting FK constraints):
-- 1. Roles and Permissions (no dependencies)
-- 2. Schools, Semesters (no dependencies)
-- 3. School_Semesters (depends on schools, semesters)
-- 4. Subjects (depends on schools)
-- 5. Users (depends on roles)
-- 6. User_Roles (depends on users, roles)
-- 7. Classes (depends on schools, semesters)
-- 8. Class_Teachers, Class_Students, Class_Subjects (depend on classes, users, subjects)
-- 9. Posts (depends on users)
-- 10. Grades (depends on users, classes)
-- 11. Other tables (personal_schedules, social_settings, login_history, password_resets)

-- ===== 1. ROLES AND PERMISSIONS =====
INSERT INTO roles (role_name, description) VALUES
(N'admin', N'Quản trị hệ thống - Toàn quyền'),
(N'teacher', N'Giáo viên - Quản lý lớp học và điểm số'),
(N'student', N'Học sinh - Xem điểm và bài viết');

INSERT INTO permissions (permission_name, description) VALUES
(N'user_management', N'Quản lý người dùng'),
(N'login', N'Đăng nhập hệ thống'),
(N'register', N'Đăng ký tài khoản'),
(N'view_posts', N'Xem bài viết'),
(N'create_posts', N'Đăng bài viết'),
(N'edit_posts', N'Sửa bài viết'),
(N'delete_posts', N'Xóa bài viết'),
(N'publish_posts', N'Xuất bản bài viết'),
(N'view_grades', N'Xem điểm'),
(N'manage_grades', N'Quản lý điểm'),
(N'export_grades', N'Xuất bảng điểm'),
(N'view_schedule', N'Xem lịch'),
(N'manage_schedule', N'Quản lý lịch'),
(N'view_personal_calendar', N'Xem lịch cá nhân'),
(N'system_settings', N'Cài đặt hệ thống'),
(N'social_settings', N'Cài đặt mạng xã hội'),
(N'privacy_settings', N'Cài đặt bảo mật'),
(N'password_reset', N'Đặt lại mật khẩu'),
(N'account_security', N'Bảo mật tài khoản'),
(N'view_login_history', N'Xem lịch sử đăng nhập');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_name = N'admin';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_name = N'teacher'
AND p.permission_name IN (N'view_posts', N'create_posts', N'edit_posts', N'publish_posts', 
                         N'view_grades', N'manage_grades', N'export_grades',
                         N'view_schedule', N'manage_schedule');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_name = N'student'
AND p.permission_name IN (N'view_posts', N'view_grades', N'view_schedule', 
                         N'view_personal_calendar', N'password_reset', N'account_security');

-- ===== 2. SCHOOLS AND SEMESTERS =====
INSERT INTO schools (school_name) VALUES
(N'Trường Đại học UIT'),
(N'Trường THPT Lê Hồng Phong'),
(N'Trường THPT Nguyễn Thị Minh Khai');

INSERT INTO semesters (semester_name) VALUES
(N'Học kỳ 1 - 2024-2025'),
(N'Học kỳ 2 - 2024-2025'),
(N'Học kỳ 3 - 2024-2025');

-- ===== 3. SCHOOL_SEMESTERS JUNCTION =====
INSERT INTO school_semesters (school_id, semester_id) VALUES
(1, 1), (1, 2), (1, 3), -- UIT
(2, 1), (2, 2), (2, 3), -- THPT Lê Hồng Phong
(3, 1), (3, 2), (3, 3); -- THPT Nguyễn Thị Minh Khai

-- ===== 4. SUBJECTS (depends on schools) =====
INSERT INTO subjects (subject_name, school_id, description) VALUES
-- UIT subjects
(N'Lập trình hướng đối tượng', 1, N'Ngôn ngữ lập trình Java/C++'),
(N'Cơ sở dữ liệu', 1, N'SQL Server/MySQL'),
(N'Mạng máy tính', 1, N'Computer Networks'),
(N'Cấu trúc dữ liệu', 1, N'Data Structures'),
(N'Hệ điều hành', 1, N'Operating Systems'),
-- THPT subjects
(N'Toán', 2, N'Đại số, Hình học, Giải tích'),
(N'Văn', 2, N'Tiếng Việt, Văn học'),
(N'Tiếng Anh', 2, N'English language'),
(N'Vật lý', 2, N'Physics'),
(N'Hóa học', 2, N'Chemistry'),
(N'Toán', 3, N'Đại số, Hình học, Giải tích'),
(N'Văn', 3, N'Tiếng Việt, Văn học'),
(N'Tiếng Anh', 3, N'English language');

-- ===== 5. USERS (depends on roles) =====
INSERT INTO users (username, password, email, full_name, date_of_birth, role_id, phone, address, is_active) 
VALUES (N'admin1', N'admin123', N'admin1@gmail.com', N'Nguyễn Văn B', '1985-03-15', 1, N'0901334567', N'128 Lý Thường Kiệt, Q10, TP.HCM', 1);

INSERT INTO users (username, password, email, full_name, date_of_birth, role_id, phone, address, is_active) VALUES
-- Giáo viên UIT
(N'gv_nguyenvana', N'teacher123', N'nguyenvana@uit.edu.vn', N'Nguyễn Văn A', '1985-03-15', 2, N'0901234567', N'123 Lý Thường Kiệt, Q10, TP.HCM', 1),
(N'gv_tranthib', N'teacher123', N'tranthib@uit.edu.vn', N'Trần Thị B', '1988-07-20', 2, N'0902234567', N'456 Nguyễn Văn Cừ, Q5, TP.HCM', 1),
(N'gv_levanc', N'teacher123', N'levanc@uit.edu.vn', N'Lê Văn C', '1982-11-10', 2, N'0903234567', N'789 Trần Hưng Đạo, Q1, TP.HCM', 1),
-- Giáo viên THPT
(N'gv_phamthid', N'teacher123', N'phamthid@lhp.edu.vn', N'Phạm Thị D', '1987-05-25', 2, N'0904234567', N'321 Lê Lợi, Q1, TP.HCM', 1),
(N'gv_hoangvane', N'teacher123', N'hoangvane@ntmk.edu.vn', N'Hoàng Văn E', '1990-09-12', 2, N'0905234567', N'654 Võ Văn Kiệt, Q6, TP.HCM', 1);

INSERT INTO users (username, password, email, full_name, date_of_birth, role_id, phone, address, is_active) VALUES
-- Học sinh SE114
(N'21520001', N'student123', N'21520001@gm.uit.edu.vn', N'Nguyễn Minh Anh', '2003-01-15', 3, N'0911111111', N'12 Nguyễn Trãi, Q5, TP.HCM', 1),
(N'21520002', N'student123', N'21520002@gm.uit.edu.vn', N'Trần Văn Bình', '2003-03-20', 3, N'0911111112', N'34 Lê Lai, Q1, TP.HCM', 1),
(N'21520003', N'student123', N'21520003@gm.uit.edu.vn', N'Lê Thị Cẩm', '2003-05-10', 3, N'0911111113', N'56 Hai Bà Trưng, Q3, TP.HCM', 1),
(N'21520004', N'student123', N'21520004@gm.uit.edu.vn', N'Phạm Quốc Dũng', '2003-07-08', 3, N'0911111114', N'78 Điện Biên Phủ, Q10, TP.HCM', 1),
-- Học sinh SE113
(N'21520011', N'student123', N'21520011@gm.uit.edu.vn', N'Võ Thị Hoa', '2003-02-14', 3, N'0911111121', N'90 Cách Mạng Tháng 8, Q3, TP.HCM', 1),
(N'21520012', N'student123', N'21520012@gm.uit.edu.vn', N'Đặng Văn Khoa', '2003-04-22', 3, N'0911111122', N'12 Trường Chinh, QTB, TP.HCM', 1),
-- Học sinh THPT
(N'hs10001', N'student123', N'hs10001@lhp.edu.vn', N'Nguyễn Văn Nam', '2008-06-15', 3, N'0912222221', N'123 Lý Tự Trọng, Q1, TP.HCM', 1),
(N'hs10002', N'student123', N'hs10002@lhp.edu.vn', N'Trần Thị Oanh', '2008-08-20', 3, N'0912222222', N'456 Pasteur, Q3, TP.HCM', 1),
(N'hs11001', N'student123', N'hs11001@ntmk.edu.vn', N'Lê Minh Phát', '2007-03-10', 3, N'0913333331', N'789 Nguyễn Đình Chiểu, Q3, TP.HCM', 1),
(N'hs11002', N'student123', N'hs11002@ntmk.edu.vn', N'Phạm Thu Quỳnh', '2007-11-25', 3, N'0913333332', N'321 Cao Thắng, Q10, TP.HCM', 1);

-- ===== 6. USER_ROLES (depends on users, roles) =====
INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(1, 1, 1);

INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(2, 2, 1), -- gv_nguyenvana
(3, 2, 1), -- gv_tranthib
(4, 2, 1), -- gv_levanc
(5, 2, 1), -- gv_phamthid
(6, 2, 1); -- gv_hoangvane

INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(7, 3, 1),  -- 21520001
(8, 3, 1),  -- 21520002
(9, 3, 1),  -- 21520003
(10, 3, 1), -- 21520004
(11, 3, 1), -- 21520011
(12, 3, 1), -- 21520012
(13, 3, 1), -- hs10001
(14, 3, 1), -- hs10002
(15, 3, 1), -- hs11001
(16, 3, 1); -- hs11002

-- ===== 7. CLASSES (depends on schools, semesters) =====
INSERT INTO classes (class_name, school_id, semester_id) VALUES
-- UIT classes
(N'SE113', 1, 1),
(N'SE114', 1, 1),
(N'SE115', 1, 1),
(N'IT001', 1, 1),
-- THPT Lê Hồng Phong
(N'10A1', 2, 1),
(N'11A2', 2, 1),
(N'12A3', 2, 1),
-- THPT Nguyễn Thị Minh Khai
(N'10B1', 3, 1),
(N'11B2', 3, 1),
(N'12B3', 3, 1);

-- ===== 8. JUNCTION TABLES (depends on classes, users, subjects) =====
INSERT INTO class_teachers (class_id, teacher_id) VALUES
(1, 2),  -- SE113 - gv_nguyenvana
(2, 2),  -- SE114 - gv_nguyenvana
(3, 3),  -- SE115 - gv_tranthib
(4, 4),  -- IT001 - gv_levanc
(5, 5),  -- 10A1 - gv_phamthid
(8, 6);  -- 10B1 - gv_hoangvane

INSERT INTO class_students (class_id, student_id) VALUES
-- SE113 students
(1, 11), -- 21520011 Võ Thị Hoa
(1, 12), -- 21520012 Đặng Văn Khoa
-- SE114 students (id=2)
(2, 7),  -- 21520001 Nguyễn Minh Anh
(2, 8),  -- 21520002 Trần Văn Bình
(2, 9),  -- 21520003 Lê Thị Cẩm
(2, 10), -- 21520004 Phạm Quốc Dũng
-- SE115 students (id=3)
-- IT001 students (id=4)
-- 10A1 students (id=5)
(5, 13), -- hs10001 Nguyễn Văn Nam
(5, 14), -- hs10002 Trần Thị Oanh
-- 10B1 students (id=8)
(8, 15), -- hs11001 Lê Minh Phát
(8, 16); -- hs11002 Phạm Thu Quỳnh

INSERT INTO class_subjects (class_id, subject_id) VALUES
-- SE114 (class_id=2): Cơ sở dữ liệu
(2, 2),
-- IT001 (class_id=4): Lập trình hướng đối tượng  
(4, 1),
-- 10A1 (class_id=5): Toán
(5, 6),
-- 10B1 (class_id=8): Tiếng Anh
(8, 8);

-- ===== 9. POSTS (depends on users) =====
INSERT INTO posts (title, content, author_id, post_type, is_published, published_at) VALUES
(N'Thông báo lịch thi học kỳ 1', N'Lịch thi học kỳ 1 năm học 2024-2025 sẽ được tổ chức từ ngày 15/01/2025. Sinh viên vui lòng xem chi tiết lịch thi trên website của trường.', 2, N'announcement', 1, GETDATE()),
(N'Hướng dẫn sử dụng hệ thống', N'Hệ thống quản lý học tập mới đã được triển khai. Vui lòng đọc kỹ hướng dẫn để sử dụng hiệu quả.', 1, N'general', 1, GETDATE()),
(N'Thông báo nghỉ lễ Tết Nguyên Đán', N'Nhà trường thông báo lịch nghỉ Tết Nguyên Đán 2025 từ ngày 25/01 đến 05/02/2025. Chúc mọi người năm mới vui vẻ!', 1, N'announcement', 1, GETDATE()),
(N'Kết quả thi giữa kỳ môn Lập trình hướng đối tượng', N'Kết quả thi giữa kỳ đã được cập nhật. Sinh viên có thể xem điểm trên hệ thống.', 2, N'grade', 1, GETDATE()),
(N'Thông báo đăng ký môn học kỳ 2', N'Thời gian đăng ký môn học kỳ 2 từ 01/12 đến 15/12/2024. Sinh viên lưu ý đăng ký đúng thời hạn.', 3, N'announcement', 1, GETDATE()),
(N'Họp phụ huynh cuối học kỳ', N'Trường tổ chức họp phụ huynh vào ngày 20/12/2024 để báo cáo kết quả học tập của các em.', 5, N'announcement', 1, GETDATE());

-- ===== 10. GRADES (depends on users, classes) =====
INSERT INTO grades (student_id, class_id, grade_value, grade_type, notes) VALUES
-- Học sinh 21520001 (Nguyễn Minh Anh) - Classes
(7, 2, 8.5, N'quá trình', N'Tham gia tích cực, làm bài tập đầy đủ'),
(7, 2, 9.0, N'giữa kỳ', N'Kết quả thi giữa kỳ tốt'),
(7, 2, 8.5, N'cuối kỳ', N'Kết quả thi cuối kỳ rất tốt'),
(7, 2, 9.0, N'thực hành', N'Thực hành rất tốt, mã code sạch'),
-- Học sinh 21520002 (Trần Văn Bình) - Classes
(8, 2, 7.0, N'quá trình', N'Tham gia đều đặn'),
(8, 2, 7.5, N'giữa kỳ', N'Kết quả khá'),
(8, 2, 8.0, N'cuối kỳ', N'Kết quả tốt'),
(8, 2, 7.5, N'thực hành', N'Thực hành tốt'),
-- Học sinh 21520003 (Lê Thị Cẩm) - Classes
(9, 2, 9.5, N'quá trình', N'Rất tích cực, làm bài tập xuất sắc'),
(9, 2, 9.5, N'giữa kỳ', N'Kết quả thi giữa kỳ xuất sắc'),
(9, 2, 9.0, N'cuối kỳ', N'Kết quả thi cuối kỳ rất tốt'),
(9, 2, 9.5, N'thực hành', N'Thực hành xuất sắc'),
-- Học sinh 21520004 (Phạm Quốc Dũng) - Classes
(10, 2, 8.0, N'quá trình', N'Tham gia tốt'),
(10, 2, 8.5, N'giữa kỳ', N'Kết quả tốt'),
(10, 2, 7.5, N'cuối kỳ', N'Kết quả khá'),
(10, 2, 8.0, N'thực hành', N'Thực hành tốt'),
-- Học sinh hs10001 - Classes
(13, 5, 8.0, N'quá trình', N'Học tập đều đặn'),
(13, 5, 8.5, N'giữa kỳ', N'Kết quả tốt'),
(13, 5, 9.0, N'cuối kỳ', N'Kết quả rất tốt'),
(13, 5, 8.5, N'thực hành', N'Thực hành tốt'),
-- Học sinh hs10002 - Classes
(14, 5, 7.0, N'quá trình', N'Tham gia bình thường'),
(14, 5, 7.5, N'giữa kỳ', N'Kết quả khá'),
(14, 5, 8.5, N'cuối kỳ', N'Kết quả tốt'),
(14, 5, 8.0, N'thực hành', N'Thực hành khá');

-- ===== 11. PERSONAL SCHEDULES (depends on users) =====
INSERT INTO personal_schedules (user_id, title, description, schedule_date, start_time, end_time, schedule_type) VALUES
-- Lịch của giáo viên
(2, N'Họp khoa', N'Họp khoa về kế hoạch học kỳ mới', '2025-01-05', '08:00', '10:00', N'meeting'),
(2, N'Chấm bài thi', N'Chấm bài thi giữa kỳ môn OOP', '2025-01-10', '13:00', '17:00', N'work'),
(3, N'Hướng dẫn đồ án', N'Hướng dẫn đồ án cho nhóm sinh viên', '2025-01-08', '14:00', '16:00', N'teaching'),
-- Lịch của học sinh
(7, N'Ôn tập giữa kỳ', N'Ôn tập môn Lập trình hướng đối tượng', '2025-01-12', '19:00', '21:00', N'study'),
(7, N'Làm bài tập nhóm', N'Họp nhóm làm project môn Database', '2025-01-15', '14:00', '17:00', N'group_work'),
(8, N'Thể thao', N'Chơi bóng đá với bạn bè', '2025-01-13', '16:00', '18:00', N'personal'),
(9, N'Học tiếng Anh', N'Lớp tiếng Anh ngoại khóa', '2025-01-14', '18:30', '20:30', N'study'),
(13, N'Học thêm Toán', N'Lớp học thêm Toán', '2025-01-11', '18:00', '20:00', N'study'),
(14, N'Thi Olympic Văn', N'Tham gia thi Olympic Văn cấp trường', '2025-01-20', '08:00', '11:00', N'exam');

-- ===== 12. SOCIAL SETTINGS (depends on users) =====
INSERT INTO social_settings (user_id, setting_key, setting_value, setting_type) VALUES
(7, N'profile_visibility', N'public', N'privacy'),
(7, N'show_email', N'friends_only', N'privacy'),
(7, N'allow_messages', N'everyone', N'privacy'),
(8, N'profile_visibility', N'friends_only', N'privacy'),
(8, N'show_email', N'private', N'privacy'),
(9, N'profile_visibility', N'public', N'privacy'),
(9, N'notification_posts', N'enabled', N'notification'),
(9, N'notification_grades', N'enabled', N'notification');

-- ===== 13. LOGIN HISTORY (depends on users) =====
INSERT INTO login_history (user_id, login_time, ip_address, user_agent, login_status) VALUES
(1, '2024-12-20 08:00:00', N'192.168.1.100', N'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', N'success'),
(2, '2024-12-20 09:15:00', N'192.168.1.101', N'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', N'success'),
(7, '2024-12-20 10:30:00', N'192.168.1.102', N'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0)', N'success'),
(8, '2024-12-20 11:00:00', N'192.168.1.103', N'Mozilla/5.0 (Android 11; Mobile)', N'success'),
(7, '2024-12-20 14:20:00', N'192.168.1.102', N'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0)', N'success'),
(9, '2024-12-20 15:45:00', N'192.168.1.104', N'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', N'success'),
(8, '2024-12-20 16:00:00', N'192.168.1.103', N'Mozilla/5.0 (Android 11; Mobile)', N'failed'),
(8, '2024-12-20 16:05:00', N'192.168.1.103', N'Mozilla/5.0 (Android 11; Mobile)', N'success');

-- ===== 14. PASSWORD RESETS (depends on users) =====
INSERT INTO password_resets (user_id, reset_token, expires_at, used) VALUES
(7, N'token_abc123xyz', DATEADD(hour, 24, GETDATE()), 0),
(8, N'token_def456uvw', DATEADD(hour, -2, GETDATE()), 1),
(9, N'token_ghi789rst', DATEADD(hour, 12, GETDATE()), 0);

GO