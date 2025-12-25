-- Sample Data for Mobile_app Database
USE Mobile_app;
GO

-- Thêm học kỳ
INSERT INTO semesters (semester_name) VALUES
(N'Học kỳ 1 - 2024-2025'),
(N'Học kỳ 2 - 2024-2025'),
(N'Học kỳ 3 - 2024-2025');

-- Thêm thêm trường
INSERT INTO schools (school_name) VALUES
(N'Trường THPT Lê Hồng Phong'),
(N'Trường THPT Nguyễn Thị Minh Khai');

-- Thêm thêm lớp
INSERT INTO classes (class_name, school_id) VALUES
-- UIT classes
(N'SE113', 1),
(N'SE115', 1),
(N'IT001', 1),
-- THPT Lê Hồng Phong
(N'10A1', 2),
(N'11A2', 2),
(N'12A3', 2),
-- THPT Nguyễn Thị Minh Khai
(N'10B1', 3),
(N'11B2', 3),
(N'12B3', 3);

-- Thêm giáo viên
INSERT INTO users (username, password, email, full_name, date_of_birth, class_id, role_id, phone, address, is_active) VALUES
-- Giáo viên UIT
(N'gv_nguyenvana', N'teacher123', N'nguyenvana@uit.edu.vn', N'Nguyễn Văn A', '1985-03-15', 1, 2, N'0901234567', N'123 Lý Thường Kiệt, Q10, TP.HCM', 1),
(N'gv_tranthib', N'teacher123', N'tranthib@uit.edu.vn', N'Trần Thị B', '1988-07-20', 1, 2, N'0902234567', N'456 Nguyễn Văn Cừ, Q5, TP.HCM', 1),
(N'gv_levanc', N'teacher123', N'levanc@uit.edu.vn', N'Lê Văn C', '1982-11-10', 1, 2, N'0903234567', N'789 Trần Hưng Đạo, Q1, TP.HCM', 1),
-- Giáo viên THPT
(N'gv_phamthid', N'teacher123', N'phamthid@lhp.edu.vn', N'Phạm Thị D', '1987-05-25', 5, 2, N'0904234567', N'321 Lê Lợi, Q1, TP.HCM', 1),
(N'gv_hoangvane', N'teacher123', N'hoangvane@ntmk.edu.vn', N'Hoàng Văn E', '1990-09-12', 8, 2, N'0905234567', N'654 Võ Văn Kiệt, Q6, TP.HCM', 1);

-- Thêm học sinh
INSERT INTO users (username, password, email, full_name, date_of_birth, class_id, role_id, phone, address, is_active) VALUES
-- Học sinh SE114
(N'21520001', N'student123', N'21520001@gm.uit.edu.vn', N'Nguyễn Minh Anh', '2003-01-15', 1, 3, N'0911111111', N'12 Nguyễn Trãi, Q5, TP.HCM', 1),
(N'21520002', N'student123', N'21520002@gm.uit.edu.vn', N'Trần Văn Bình', '2003-03-20', 1, 3, N'0911111112', N'34 Lê Lai, Q1, TP.HCM', 1),
(N'21520003', N'student123', N'21520003@gm.uit.edu.vn', N'Lê Thị Cẩm', '2003-05-10', 1, 3, N'0911111113', N'56 Hai Bà Trưng, Q3, TP.HCM', 1),
(N'21520004', N'student123', N'21520004@gm.uit.edu.vn', N'Phạm Quốc Dũng', '2003-07-08', 1, 3, N'0911111114', N'78 Điện Biên Phủ, Q10, TP.HCM', 1),
-- Học sinh SE113
(N'21520011', N'student123', N'21520011@gm.uit.edu.vn', N'Võ Thị Hoa', '2003-02-14', 2, 3, N'0911111121', N'90 Cách Mạng Tháng 8, Q3, TP.HCM', 1),
(N'21520012', N'student123', N'21520012@gm.uit.edu.vn', N'Đặng Văn Khoa', '2003-04-22', 2, 3, N'0911111122', N'12 Trường Chinh, QTB, TP.HCM', 1),
-- Học sinh THPT
(N'hs10001', N'student123', N'hs10001@lhp.edu.vn', N'Nguyễn Văn Nam', '2008-06-15', 5, 3, N'0912222221', N'123 Lý Tự Trọng, Q1, TP.HCM', 1),
(N'hs10002', N'student123', N'hs10002@lhp.edu.vn', N'Trần Thị Oanh', '2008-08-20', 5, 3, N'0912222222', N'456 Pasteur, Q3, TP.HCM', 1),
(N'hs11001', N'student123', N'hs11001@ntmk.edu.vn', N'Lê Minh Phát', '2007-03-10', 8, 3, N'0913333331', N'789 Nguyễn Đình Chiểu, Q3, TP.HCM', 1),
(N'hs11002', N'student123', N'hs11002@ntmk.edu.vn', N'Phạm Thu Quỳnh', '2007-11-25', 8, 3, N'0913333332', N'321 Cao Thắng, Q10, TP.HCM', 1);

-- Gán quyền cho giáo viên
INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(2, 2, 1), -- gv_nguyenvana
(3, 2, 1), -- gv_tranthib
(4, 2, 1), -- gv_levanc
(5, 2, 1), -- gv_phamthid
(6, 2, 1); -- gv_hoangvane

-- Gán quyền cho học sinh
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

-- Thêm bài viết
INSERT INTO posts (title, content, author_id, post_type, is_published, published_at) VALUES
(N'Thông báo lịch thi học kỳ 1', N'Lịch thi học kỳ 1 năm học 2024-2025 sẽ được tổ chức từ ngày 15/01/2025. Sinh viên vui lòng xem chi tiết lịch thi trên website của trường.', 2, N'announcement', 1, GETDATE()),
(N'Hướng dẫn sử dụng hệ thống', N'Hệ thống quản lý học tập mới đã được triển khai. Vui lòng đọc kỹ hướng dẫn để sử dụng hiệu quả.', 1, N'general', 1, GETDATE()),
(N'Thông báo nghỉ lễ Tết Nguyên Đán', N'Nhà trường thông báo lịch nghỉ Tết Nguyên Đán 2025 từ ngày 25/01 đến 05/02/2025. Chúc mọi người năm mới vui vẻ!', 1, N'announcement', 1, GETDATE()),
(N'Kết quả thi giữa kỳ môn Lập trình hướng đối tượng', N'Kết quả thi giữa kỳ đã được cập nhật. Sinh viên có thể xem điểm trên hệ thống.', 2, N'grade', 1, GETDATE()),
(N'Thông báo đăng ký môn học kỳ 2', N'Thời gian đăng ký môn học kỳ 2 từ 01/12 đến 15/12/2024. Sinh viên lưu ý đăng ký đúng thời hạn.', 3, N'announcement', 1, GETDATE()),
(N'Họp phụ huynh cuối học kỳ', N'Trường tổ chức họp phụ huynh vào ngày 20/12/2024 để báo cáo kết quả học tập của các em.', 5, N'announcement', 1, GETDATE());

-- Thêm điểm cho học sinh SE114
INSERT INTO grades (student_id, class_id, subject_name, grade_value, grade_type, semester_id, school_year, teacher_id) VALUES
-- Học sinh 21520001 (Nguyễn Minh Anh)
(7, 1, N'Lập trình hướng đối tượng', 8.5, N'midterm', 1, N'2024-2025', 2),
(7, 1, N'Lập trình hướng đối tượng', 9.0, N'final', 1, N'2024-2025', 2),
(7, 1, N'Cơ sở dữ liệu', 7.5, N'midterm', 1, N'2024-2025', 3),
(7, 1, N'Cơ sở dữ liệu', 8.0, N'final', 1, N'2024-2025', 3),
(7, 1, N'Mạng máy tính', 9.0, N'midterm', 1, N'2024-2025', 4),
(7, 1, N'Mạng máy tính', 9.5, N'final', 1, N'2024-2025', 4),
-- Học sinh 21520002 (Trần Văn Bình)
(8, 1, N'Lập trình hướng đối tượng', 7.0, N'midterm', 1, N'2024-2025', 2),
(8, 1, N'Lập trình hướng đối tượng', 7.5, N'final', 1, N'2024-2025', 2),
(8, 1, N'Cơ sở dữ liệu', 8.0, N'midterm', 1, N'2024-2025', 3),
(8, 1, N'Cơ sở dữ liệu', 8.5, N'final', 1, N'2024-2025', 3),
(8, 1, N'Mạng máy tính', 6.5, N'midterm', 1, N'2024-2025', 4),
(8, 1, N'Mạng máy tính', 7.0, N'final', 1, N'2024-2025', 4),
-- Học sinh 21520003 (Lê Thị Cẩm)
(9, 1, N'Lập trình hướng đối tượng', 9.5, N'midterm', 1, N'2024-2025', 2),
(9, 1, N'Lập trình hướng đối tượng', 9.5, N'final', 1, N'2024-2025', 2),
(9, 1, N'Cơ sở dữ liệu', 9.0, N'midterm', 1, N'2024-2025', 3),
(9, 1, N'Cơ sở dữ liệu', 9.5, N'final', 1, N'2024-2025', 3),
(9, 1, N'Mạng máy tính', 8.5, N'midterm', 1, N'2024-2025', 4),
(9, 1, N'Mạng máy tính', 9.0, N'final', 1, N'2024-2025', 4);

-- Thêm điểm cho học sinh THPT
INSERT INTO grades (student_id, class_id, subject_name, grade_value, grade_type, semester_id, school_year, teacher_id) VALUES
-- Học sinh hs10001
(13, 5, N'Toán', 8.0, N'midterm', 1, N'2024-2025', 5),
(13, 5, N'Toán', 8.5, N'final', 1, N'2024-2025', 5),
(13, 5, N'Văn', 7.5, N'midterm', 1, N'2024-2025', 5),
(13, 5, N'Văn', 8.0, N'final', 1, N'2024-2025', 5),
(13, 5, N'Tiếng Anh', 9.0, N'midterm', 1, N'2024-2025', 5),
(13, 5, N'Tiếng Anh', 9.0, N'final', 1, N'2024-2025', 5),
-- Học sinh hs10002
(14, 5, N'Toán', 7.0, N'midterm', 1, N'2024-2025', 5),
(14, 5, N'Toán', 7.5, N'final', 1, N'2024-2025', 5),
(14, 5, N'Văn', 8.5, N'midterm', 1, N'2024-2025', 5),
(14, 5, N'Văn', 9.0, N'final', 1, N'2024-2025', 5),
(14, 5, N'Tiếng Anh', 8.0, N'midterm', 1, N'2024-2025', 5),
(14, 5, N'Tiếng Anh', 8.5, N'final', 1, N'2024-2025', 5);

-- Thêm lịch cá nhân
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

-- Thêm cài đặt mạng xã hội
INSERT INTO social_settings (user_id, setting_key, setting_value, setting_type) VALUES
(7, N'profile_visibility', N'public', N'privacy'),
(7, N'show_email', N'friends_only', N'privacy'),
(7, N'allow_messages', N'everyone', N'privacy'),
(8, N'profile_visibility', N'friends_only', N'privacy'),
(8, N'show_email', N'private', N'privacy'),
(9, N'profile_visibility', N'public', N'privacy'),
(9, N'notification_posts', N'enabled', N'notification'),
(9, N'notification_grades', N'enabled', N'notification');

-- Thêm lịch sử đăng nhập
INSERT INTO login_history (user_id, login_time, ip_address, user_agent, login_status) VALUES
(1, '2024-12-20 08:00:00', N'192.168.1.100', N'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', N'success'),
(2, '2024-12-20 09:15:00', N'192.168.1.101', N'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', N'success'),
(7, '2024-12-20 10:30:00', N'192.168.1.102', N'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0)', N'success'),
(8, '2024-12-20 11:00:00', N'192.168.1.103', N'Mozilla/5.0 (Android 11; Mobile)', N'success'),
(7, '2024-12-20 14:20:00', N'192.168.1.102', N'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0)', N'success'),
(9, '2024-12-20 15:45:00', N'192.168.1.104', N'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', N'success'),
(8, '2024-12-20 16:00:00', N'192.168.1.103', N'Mozilla/5.0 (Android 11; Mobile)', N'failed'),
(8, '2024-12-20 16:05:00', N'192.168.1.103', N'Mozilla/5.0 (Android 11; Mobile)', N'success');

-- Thêm token đặt lại mật khẩu (một số đã sử dụng, một số còn hiệu lực)
INSERT INTO password_resets (user_id, reset_token, expires_at, used) VALUES
(7, N'token_abc123xyz', DATEADD(hour, 24, GETDATE()), 0),
(8, N'token_def456uvw', DATEADD(hour, -2, GETDATE()), 1),
(9, N'token_ghi789rst', DATEADD(hour, 12, GETDATE()), 0);

GO