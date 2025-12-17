USE Mobile_app;
GO

-- =====================================================
-- INSERT SAMPLE USERS
-- =====================================================

-- Admin already exists (username: Danh)

-- Insert Teachers
INSERT INTO users (username, password, email, full_name, date_of_birth, phone, address, is_active) VALUES
(N'nguyenvana', N'teacher123', N'nguyenvana@school.edu.vn', N'Nguyễn Văn A', '1985-03-15', N'0901234567', N'123 Lê Lợi, Q1, TP.HCM', 1),
(N'tranthib', N'teacher123', N'tranthib@school.edu.vn', N'Trần Thị B', '1987-07-20', N'0902345678', N'456 Nguyễn Huệ, Q1, TP.HCM', 1),
(N'levanc', N'teacher123', N'levanc@school.edu.vn', N'Lê Văn C', '1990-11-10', N'0903456789', N'789 Pasteur, Q3, TP.HCM', 1),
(N'phamthid', N'teacher123', N'phamthid@school.edu.vn', N'Phạm Thị D', '1988-05-25', N'0904567890', N'321 Võ Văn Tần, Q3, TP.HCM', 1);

-- Insert Students
INSERT INTO users (username, password, email, full_name, date_of_birth, phone, address, is_active) VALUES
(N'student001', N'student123', N'nguyenminhe@student.edu.vn', N'Nguyễn Minh E', '2008-01-15', N'0911111111', N'12 Trần Hưng Đạo, Q5, TP.HCM', 1),
(N'student002', N'student123', N'tranthif@student.edu.vn', N'Trần Thị F', '2008-03-20', N'0922222222', N'34 Lý Thường Kiệt, Q10, TP.HCM', 1),
(N'student003', N'student123', N'levang@student.edu.vn', N'Lê Văn G', '2008-05-10', N'0933333333', N'56 Hai Bà Trưng, Q1, TP.HCM', 1),
(N'student004', N'student123', N'phamthih@student.edu.vn', N'Phạm Thị H', '2008-07-08', N'0944444444', N'78 Điện Biên Phủ, Q3, TP.HCM', 1),
(N'student005', N'student123', N'hoangvani@student.edu.vn', N'Hoàng Văn I', '2008-09-12', N'0955555555', N'90 Cách Mạng Tháng 8, Q10, TP.HCM', 1),
(N'student006', N'student123', N'vuthik@student.edu.vn', N'Vũ Thị K', '2008-11-25', N'0966666666', N'11 Nguyễn Thị Minh Khai, Q1, TP.HCM', 1),
(N'student007', N'student123', N'dovanl@student.edu.vn', N'Đỗ Văn L', '2008-02-14', N'0977777777', N'22 Lê Duẩn, Q1, TP.HCM', 1),
(N'student008', N'student123', N'buithim@student.edu.vn', N'Bùi Thị M', '2008-04-18', N'0988888888', N'33 Võ Thị Sáu, Q3, TP.HCM', 1);

GO

-- =====================================================
-- ASSIGN ROLES TO USERS
-- =====================================================

-- Assign teacher role (role_id = 2)
INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(2, 2, 1), -- Nguyễn Văn A
(3, 2, 1), -- Trần Thị B
(4, 2, 1), -- Lê Văn C
(5, 2, 1); -- Phạm Thị D

-- Assign student role (role_id = 3)
INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(6, 3, 1),  -- student001
(7, 3, 1),  -- student002
(8, 3, 1),  -- student003
(9, 3, 1),  -- student004
(10, 3, 1), -- student005
(11, 3, 1), -- student006
(12, 3, 1), -- student007
(13, 3, 1); -- student008

GO

-- =====================================================
-- INSERT POSTS
-- =====================================================

INSERT INTO posts (title, content, author_id, post_type, is_published, published_at) VALUES
(N'Thông báo lịch nghỉ Tết Nguyên Đán 2025', 
 N'Kính gửi quý phụ huynh và các em học sinh, Nhà trường thông báo lịch nghỉ Tết Nguyên Đán 2025 từ ngày 25/01 đến 02/02/2025. Học sinh trở lại học vào ngày 03/02/2025.',
 1, N'announcement', 1, '2024-12-10 09:00:00'),

(N'Kế hoạch thi học kỳ I năm học 2024-2025',
 N'Thời gian thi học kỳ I sẽ diễn ra từ ngày 18/12 đến 24/12/2024. Học sinh cần chuẩn bị tài liệu và ôn tập kỹ lưỡng.',
 2, N'announcement', 1, '2024-12-05 10:30:00'),

(N'Hướng dẫn ôn tập môn Toán lớp 10',
 N'Các em học sinh lưu ý ôn tập các chương: Hàm số bậc nhất, phương trình bậc hai, và bất phương trình. Tài liệu đính kèm trong file PDF.',
 2, N'general', 1, '2024-12-08 14:00:00'),

(N'Thông báo về buổi họp phụ huynh',
 N'Buổi họp phụ huynh học kỳ I sẽ được tổ chức vào ngày 28/12/2024 lúc 8:00 sáng tại hội trường nhà trường.',
 1, N'announcement', 1, '2024-12-12 08:00:00'),

(N'Bài tập về nhà môn Văn tuần 15',
 N'Học sinh hoàn thành bài tập phân tích tác phẩm "Chiếc lược ngà" và nộp trước ngày 20/12/2024.',
 3, N'general', 1, '2024-12-13 15:30:00'),

(N'Chúc mừng học sinh đạt giải Olympic',
 N'Xin chúc mừng em Nguyễn Minh E và em Lê Văn G đã đạt giải Nhì Olympic Toán cấp Thành phố. Chúc các em tiếp tục phát huy!',
 1, N'announcement', 1, '2024-12-14 16:00:00'),

(N'Lịch thi đấu bóng đá liên trường',
 N'Đội tuyển bóng đá trường sẽ tham gia giải đấu liên trường vào ngày 22/12/2024. Mời các bạn đến cổ vũ!',
 4, N'general', 1, '2024-12-11 11:00:00');

GO

-- =====================================================
-- INSERT GRADES
-- =====================================================

-- Grades for student001 (user_id = 6)
INSERT INTO grades (student_id, subject_name, grade_value, grade_type, semester, school_year, teacher_id) VALUES
(6, N'Toán', 8.5, N'midterm', N'HK1', N'2024-2025', 2),
(6, N'Toán', 9.0, N'final', N'HK1', N'2024-2025', 2),
(6, N'Văn', 7.5, N'midterm', N'HK1', N'2024-2025', 3),
(6, N'Văn', 8.0, N'final', N'HK1', N'2024-2025', 3),
(6, N'Anh', 9.5, N'midterm', N'HK1', N'2024-2025', 4),
(6, N'Anh', 9.0, N'final', N'HK1', N'2024-2025', 4),
(6, N'Lý', 8.0, N'midterm', N'HK1', N'2024-2025', 5),
(6, N'Lý', 8.5, N'final', N'HK1', N'2024-2025', 5);

-- Grades for student002 (user_id = 7)
INSERT INTO grades (student_id, subject_name, grade_value, grade_type, semester, school_year, teacher_id) VALUES
(7, N'Toán', 7.0, N'midterm', N'HK1', N'2024-2025', 2),
(7, N'Toán', 7.5, N'final', N'HK1', N'2024-2025', 2),
(7, N'Văn', 8.5, N'midterm', N'HK1', N'2024-2025', 3),
(7, N'Văn', 9.0, N'final', N'HK1', N'2024-2025', 3),
(7, N'Anh', 8.0, N'midterm', N'HK1', N'2024-2025', 4),
(7, N'Anh', 8.5, N'final', N'HK1', N'2024-2025', 4),
(7, N'Lý', 7.5, N'midterm', N'HK1', N'2024-2025', 5),
(7, N'Lý', 8.0, N'final', N'HK1', N'2024-2025', 5);

-- Grades for student003 (user_id = 8)
INSERT INTO grades (student_id, subject_name, grade_value, grade_type, semester, school_year, teacher_id) VALUES
(8, N'Toán', 9.0, N'midterm', N'HK1', N'2024-2025', 2),
(8, N'Toán', 9.5, N'final', N'HK1', N'2024-2025', 2),
(8, N'Văn', 8.0, N'midterm', N'HK1', N'2024-2025', 3),
(8, N'Văn', 8.5, N'final', N'HK1', N'2024-2025', 3),
(8, N'Anh', 9.0, N'midterm', N'HK1', N'2024-2025', 4),
(8, N'Anh', 9.5, N'final', N'HK1', N'2024-2025', 4),
(8, N'Lý', 9.0, N'midterm', N'HK1', N'2024-2025', 5),
(8, N'Lý', 9.0, N'final', N'HK1', N'2024-2025', 5);

-- Grades for student004 (user_id = 9)
INSERT INTO grades (student_id, subject_name, grade_value, grade_type, semester, school_year, teacher_id) VALUES
(9, N'Toán', 6.5, N'midterm', N'HK1', N'2024-2025', 2),
(9, N'Toán', 7.0, N'final', N'HK1', N'2024-2025', 2),
(9, N'Văn', 7.0, N'midterm', N'HK1', N'2024-2025', 3),
(9, N'Văn', 7.5, N'final', N'HK1', N'2024-2025', 3),
(9, N'Anh', 7.5, N'midterm', N'HK1', N'2024-2025', 4),
(9, N'Anh', 8.0, N'final', N'HK1', N'2024-2025', 4),
(9, N'Lý', 6.0, N'midterm', N'HK1', N'2024-2025', 5),
(9, N'Lý', 6.5, N'final', N'HK1', N'2024-2025', 5);

-- Grades for student005 (user_id = 10)
INSERT INTO grades (student_id, subject_name, grade_value, grade_type, semester, school_year, teacher_id) VALUES
(10, N'Toán', 8.0, N'midterm', N'HK1', N'2024-2025', 2),
(10, N'Toán', 8.5, N'final', N'HK1', N'2024-2025', 2),
(10, N'Văn', 9.0, N'midterm', N'HK1', N'2024-2025', 3),
(10, N'Văn', 9.5, N'final', N'HK1', N'2024-2025', 3),
(10, N'Anh', 8.5, N'midterm', N'HK1', N'2024-2025', 4),
(10, N'Anh', 9.0, N'final', N'HK1', N'2024-2025', 4),
(10, N'Lý', 7.5, N'midterm', N'HK1', N'2024-2025', 5),
(10, N'Lý', 8.0, N'final', N'HK1', N'2024-2025', 5);

GO

-- =====================================================
-- INSERT PERSONAL SCHEDULES
-- =====================================================

-- Schedules for teachers
INSERT INTO personal_schedules (user_id, title, description, schedule_date, start_time, end_time, schedule_type) VALUES
(2, N'Dạy lớp 10A1', N'Chương trình Toán đại số', '2024-12-17', '07:30', '09:00', N'teaching'),
(2, N'Họp tổ chuyên môn', N'Thảo luận kế hoạch giảng dạy HK2', '2024-12-18', '14:00', '16:00', N'meeting'),
(3, N'Dạy lớp 10A2', N'Phân tích tác phẩm văn học', '2024-12-17', '09:15', '10:45', N'teaching'),
(3, N'Chấm bài kiểm tra', N'Chấm bài kiểm tra 15 phút', '2024-12-19', '15:00', '17:00', N'personal'),
(4, N'Dạy lớp 10A1', N'Tiếng Anh giao tiếp', '2024-12-17', '13:00', '14:30', N'teaching'),
(5, N'Dạy lớp 10A3', N'Thí nghiệm Vật lý', '2024-12-18', '07:30', '09:00', N'teaching');

-- Schedules for students
INSERT INTO personal_schedules (user_id, title, description, schedule_date, start_time, end_time, schedule_type) VALUES
(6, N'Ôn thi Toán', N'Ôn tập chương hàm số', '2024-12-17', '19:00', '21:00', N'study'),
(6, N'Thi giữa kỳ Toán', N'Phòng thi A1', '2024-12-20', '08:00', '10:00', N'exam'),
(7, N'Học nhóm môn Văn', N'Thảo luận đề thi', '2024-12-18', '15:00', '17:00', N'study'),
(7, N'Thi giữa kỳ Văn', N'Phòng thi B2', '2024-12-21', '08:00', '10:00', N'exam'),
(8, N'Luyện Olympic Toán', N'Giải đề thi năm trước', '2024-12-17', '16:00', '18:00', N'study'),
(8, N'Thi giữa kỳ Anh', N'Phòng thi C1', '2024-12-22', '13:00', '15:00', N'exam'),
(9, N'Học bù môn Lý', N'Học thêm với thầy', '2024-12-19', '14:00', '16:00', N'tutoring'),
(10, N'Sinh nhật bạn', N'Dự tiệc sinh nhật', '2024-12-23', '18:00', '21:00', N'personal');

GO

-- =====================================================
-- INSERT SOCIAL SETTINGS
-- =====================================================

INSERT INTO social_settings (user_id, setting_key, setting_value, setting_type) VALUES
(6, N'profile_visibility', N'public', N'privacy'),
(6, N'show_grades', N'friends_only', N'privacy'),
(6, N'allow_messages', N'true', N'communication'),
(7, N'profile_visibility', N'friends_only', N'privacy'),
(7, N'show_grades', N'private', N'privacy'),
(8, N'profile_visibility', N'public', N'privacy'),
(8, N'allow_comments', N'true', N'communication'),
(9, N'profile_visibility', N'private', N'privacy'),
(10, N'notification_enabled', N'true', N'notification');

GO

-- =====================================================
-- INSERT LOGIN HISTORY
-- =====================================================

INSERT INTO login_history (user_id, login_time, ip_address, user_agent, login_status) VALUES
(1, '2024-12-16 08:00:00', N'192.168.1.100', N'Mozilla/5.0 (Windows NT 10.0)', N'success'),
(2, '2024-12-16 07:30:00', N'192.168.1.101', N'Mozilla/5.0 (Windows NT 10.0)', N'success'),
(3, '2024-12-16 08:15:00', N'192.168.1.102', N'Mozilla/5.0 (Macintosh)', N'success'),
(6, '2024-12-16 06:45:00', N'192.168.1.150', N'Mozilla/5.0 (Android 12)', N'success'),
(7, '2024-12-16 07:00:00', N'192.168.1.151', N'Mozilla/5.0 (iPhone)', N'success'),
(8, '2024-12-16 07:20:00', N'192.168.1.152', N'Mozilla/5.0 (Android 13)', N'success'),
(6, '2024-12-15 09:30:00', N'192.168.1.150', N'Mozilla/5.0 (Android 12)', N'failed'),
(9, '2024-12-16 08:30:00', N'192.168.1.153', N'Mozilla/5.0 (iPhone)', N'success');

GO

-- =====================================================
-- INSERT PASSWORD RESET TOKENS (for testing)
-- =====================================================

INSERT INTO password_resets (user_id, reset_token, expires_at, used) VALUES
(6, N'abc123def456', DATEADD(hour, 24, GETDATE()), 0),
(7, N'xyz789uvw012', DATEADD(hour, 24, GETDATE()), 0);

GO