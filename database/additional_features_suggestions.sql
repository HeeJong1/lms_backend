-- ============================================
-- LMS 시스템 추가 기능 제안 스키마
-- ============================================
-- 아래는 추가하면 좋을 기능들의 데이터베이스 스키마 예시입니다.
-- 실제 구현 시 필요에 따라 선택하여 추가하세요.

-- ============================================
-- 1. 성적 관리 시스템
-- ============================================
CREATE TABLE IF NOT EXISTS grades (
    grade_id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    midterm_score INTEGER DEFAULT 0,        -- 중간고사 점수
    final_score INTEGER DEFAULT 0,          -- 기말고사 점수
    assignment_score INTEGER DEFAULT 0,     -- 과제 점수
    attendance_score INTEGER DEFAULT 0,     -- 출석 점수
    total_score DECIMAL(5,2),                -- 총점
    letter_grade VARCHAR(2),                 -- 등급 (A+, A, B+, B, C+, C, D+, D, F)
    gpa DECIMAL(3,2),                       -- 평점 (4.5 만점 기준)
    remarks TEXT,                           -- 비고
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE(enrollment_id)
);

CREATE INDEX IF NOT EXISTS idx_grades_student ON grades(student_id);
CREATE INDEX IF NOT EXISTS idx_grades_course ON grades(course_id);

-- ============================================
-- 2. 출석 관리 시스템
-- ============================================
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE(enrollment_id, attendance_date)
);

CREATE INDEX IF NOT EXISTS idx_attendance_student ON attendance(student_id);
CREATE INDEX IF NOT EXISTS idx_attendance_course ON attendance(course_id);
CREATE INDEX IF NOT EXISTS idx_attendance_date ON attendance(attendance_date);

-- ============================================
-- 3. 공지사항 시스템
-- ============================================
CREATE TABLE IF NOT EXISTS announcements (
    announcement_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_important BOOLEAN DEFAULT FALSE,
    target_role VARCHAR(20),                 -- 대상 역할 (STUDENT, INSTRUCTOR, ALL)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_announcements_course ON announcements(course_id);
CREATE INDEX IF NOT EXISTS idx_announcements_author ON announcements(author_id);
CREATE INDEX IF NOT EXISTS idx_announcements_created ON announcements(created_at DESC);

-- ============================================
-- 4. 강의 자료 관리
-- ============================================
CREATE TABLE IF NOT EXISTS course_materials (
    material_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    uploader_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    file_path VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    file_type VARCHAR(50),
    download_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (uploader_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_materials_course ON course_materials(course_id);

-- ============================================
-- 5. 과제 관리 시스템
-- ============================================
CREATE TABLE IF NOT EXISTS assignments (
    assignment_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    due_date TIMESTAMP,
    max_score INTEGER DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS assignment_submissions (
    submission_id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrollment_id BIGINT NOT NULL,
    content TEXT,
    file_path VARCHAR(500),
    file_name VARCHAR(255),
    score INTEGER,
    feedback TEXT,
    status VARCHAR(20) DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED', 'GRADED', 'RETURNED')),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    graded_at TIMESTAMP,
    FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    UNIQUE(assignment_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_assignments_course ON assignments(course_id);
CREATE INDEX IF NOT EXISTS idx_submissions_assignment ON assignment_submissions(assignment_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student ON assignment_submissions(student_id);

-- ============================================
-- 6. 시간표 관리
-- ============================================
CREATE TABLE IF NOT EXISTS course_schedules (
    schedule_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    day_of_week INTEGER NOT NULL CHECK (day_of_week BETWEEN 0 AND 6), -- 0=일요일, 1=월요일, ...
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_schedules_course ON course_schedules(course_id);

-- ============================================
-- 7. 학기 관리
-- ============================================
CREATE TABLE IF NOT EXISTS semesters (
    semester_id BIGSERIAL PRIMARY KEY,
    year INTEGER NOT NULL,
    semester_number INTEGER NOT NULL CHECK (semester_number IN (1, 2)), -- 1학기, 2학기
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(year, semester_number)
);

-- 강의에 학기 정보 추가
ALTER TABLE courses ADD COLUMN IF NOT EXISTS semester_id BIGINT;
ALTER TABLE courses ADD CONSTRAINT fk_courses_semester FOREIGN KEY (semester_id) REFERENCES semesters(semester_id);

-- 수강신청에 학기 정보 추가
ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS semester_id BIGINT;
ALTER TABLE enrollments ADD CONSTRAINT fk_enrollments_semester FOREIGN KEY (semester_id) REFERENCES semesters(semester_id);

-- ============================================
-- 8. 선수과목 관리
-- ============================================
CREATE TABLE IF NOT EXISTS prerequisites (
    prerequisite_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    prerequisite_course_id BIGINT NOT NULL,
    is_required BOOLEAN DEFAULT TRUE,        -- 필수/선택
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE(course_id, prerequisite_course_id),
    CHECK (course_id != prerequisite_course_id)
);

CREATE INDEX IF NOT EXISTS idx_prerequisites_course ON prerequisites(course_id);

-- ============================================
-- 9. 알림 시스템
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50),                        -- ENROLLMENT_APPROVED, GRADE_POSTED, ASSIGNMENT_DUE 등
    is_read BOOLEAN DEFAULT FALSE,
    related_id BIGINT,                      -- 관련 엔티티 ID (enrollment_id, grade_id 등)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_created ON notifications(created_at DESC);

-- ============================================
-- 10. 질문/답변 (Q&A) 시스템
-- ============================================
CREATE TABLE IF NOT EXISTS questions (
    question_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_answered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS answers (
    answer_id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_questions_course ON questions(course_id);
CREATE INDEX IF NOT EXISTS idx_questions_student ON questions(student_id);
CREATE INDEX IF NOT EXISTS idx_answers_question ON answers(question_id);

-- ============================================
-- 11. 통계 및 리포트
-- ============================================
-- 뷰: 학생별 성적 통계
CREATE OR REPLACE VIEW student_grade_statistics AS
SELECT 
    u.user_id,
    u.name,
    u.grade,
    COUNT(DISTINCT g.grade_id) as total_courses,
    AVG(g.total_score) as average_score,
    AVG(g.gpa) as average_gpa,
    SUM(c.credits) as total_credits
FROM users u
LEFT JOIN enrollments e ON u.user_id = e.student_id AND e.status = 'APPROVED'
LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id
LEFT JOIN courses c ON e.course_id = c.course_id
WHERE u.role = 'STUDENT'
GROUP BY u.user_id, u.name, u.grade;

-- 뷰: 강의별 통계
CREATE OR REPLACE VIEW course_statistics AS
SELECT 
    c.course_id,
    c.course_code,
    c.course_name,
    c.max_students,
    c.current_students,
    COUNT(DISTINCT e.student_id) as enrolled_students,
    COUNT(DISTINCT g.grade_id) as graded_students,
    AVG(g.total_score) as average_score,
    COUNT(DISTINCT a.assignment_id) as total_assignments
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id AND e.status = 'APPROVED'
LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id
LEFT JOIN assignments a ON c.course_id = a.course_id
GROUP BY c.course_id, c.course_code, c.course_name, c.max_students, c.current_students;
