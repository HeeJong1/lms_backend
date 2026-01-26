-- ============================================
-- LMS 데이터베이스 완전한 스키마 (모든 기능 포함)
-- ============================================
-- 이 파일은 LMS 시스템의 모든 기능을 포함한 완전한 데이터베이스 스키마입니다.
-- 실행 방법: psql -U postgres -d lms -f complete_schema.sql
-- 또는 PostgreSQL 클라이언트에서 이 파일의 모든 내용을 실행하세요.

-- ============================================
-- 1. 기본 테이블 (사용자, 강의, 수강신청)
-- ============================================

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'INSTRUCTOR', 'ADMIN')),
    grade INTEGER CHECK (grade IN (1, 2, 3, 4)),  -- 학년
    total_credits INTEGER NOT NULL DEFAULT 0,       -- 총 이수 학점
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 강의 테이블
CREATE TABLE IF NOT EXISTS courses (
    course_id BIGSERIAL PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(200) NOT NULL,
    description TEXT,
    instructor_id BIGINT NOT NULL,
    max_students INTEGER NOT NULL DEFAULT 30,
    current_students INTEGER NOT NULL DEFAULT 0,
    credits INTEGER NOT NULL DEFAULT 3,             -- 학점
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED', 'FULL')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 수강신청 테이블
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    credits INTEGER,                                 -- 학점 (강의의 학점 복사)
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    rejection_reason TEXT,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE(student_id, course_id)
);

-- ============================================
-- 2. 추가 기능 테이블
-- ============================================

-- 성적 관리 시스템
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

-- 출석 관리 시스템
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

-- 공지사항 시스템
CREATE TABLE IF NOT EXISTS announcements (
    announcement_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_important BOOLEAN DEFAULT FALSE,
    target_role VARCHAR(20),                 -- 대상 역할 (STUDENT, INSTRUCTOR, ALL, null=전체)
    view_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 강의 자료 관리
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
    category VARCHAR(50),                    -- 자료 카테고리 (강의자료, 참고자료, 기타)
    download_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (uploader_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 과제 관리 시스템
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

-- ============================================
-- 3. 인덱스 생성
-- ============================================

-- 기본 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_courses_instructor ON courses(instructor_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_course ON enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_status ON enrollments(status);

-- 성적 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_grades_student ON grades(student_id);
CREATE INDEX IF NOT EXISTS idx_grades_course ON grades(course_id);
CREATE INDEX IF NOT EXISTS idx_grades_enrollment ON grades(enrollment_id);

-- 출석 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_attendance_student ON attendance(student_id);
CREATE INDEX IF NOT EXISTS idx_attendance_course ON attendance(course_id);
CREATE INDEX IF NOT EXISTS idx_attendance_date ON attendance(attendance_date);

-- 공지사항 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_announcements_course ON announcements(course_id);
CREATE INDEX IF NOT EXISTS idx_announcements_author ON announcements(author_id);
CREATE INDEX IF NOT EXISTS idx_announcements_created ON announcements(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_announcements_important ON announcements(is_important DESC);

-- 강의 자료 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_materials_course ON course_materials(course_id);
CREATE INDEX IF NOT EXISTS idx_materials_category ON course_materials(category);

-- 과제 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_assignments_course ON assignments(course_id);
CREATE INDEX IF NOT EXISTS idx_submissions_assignment ON assignment_submissions(assignment_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student ON assignment_submissions(student_id);
CREATE INDEX IF NOT EXISTS idx_submissions_status ON assignment_submissions(status);

-- ============================================
-- 4. 함수 정의
-- ============================================

-- 함수: 학생의 총 학점 계산
CREATE OR REPLACE FUNCTION calculate_student_total_credits(student_id_param BIGINT)
RETURNS INTEGER AS $$
DECLARE
    total INTEGER;
BEGIN
    SELECT COALESCE(SUM(COALESCE(e.credits, c.credits, 0)), 0)
    INTO total
    FROM enrollments e
    JOIN courses c ON e.course_id = c.course_id
    WHERE e.student_id = student_id_param
      AND e.status = 'APPROVED';
    
    RETURN total;
END;
$$ LANGUAGE plpgsql;

-- 함수: 학생의 현재 학기 신청 학점 계산
CREATE OR REPLACE FUNCTION calculate_student_semester_credits(student_id_param BIGINT)
RETURNS INTEGER AS $$
DECLARE
    semester_total INTEGER;
BEGIN
    -- 현재 학기: 올해 1월~6월은 1학기, 7월~12월은 2학기로 가정
    SELECT COALESCE(SUM(COALESCE(e.credits, c.credits, 0)), 0)
    INTO semester_total
    FROM enrollments e
    JOIN courses c ON e.course_id = c.course_id
    WHERE e.student_id = student_id_param
      AND e.status IN ('PENDING', 'APPROVED')
      AND EXTRACT(YEAR FROM e.applied_at) = EXTRACT(YEAR FROM CURRENT_DATE)
      AND (
        (EXTRACT(MONTH FROM e.applied_at) BETWEEN 1 AND 6 AND EXTRACT(MONTH FROM CURRENT_DATE) BETWEEN 1 AND 6)
        OR
        (EXTRACT(MONTH FROM e.applied_at) BETWEEN 7 AND 12 AND EXTRACT(MONTH FROM CURRENT_DATE) BETWEEN 7 AND 12)
      );
    
    RETURN semester_total;
END;
$$ LANGUAGE plpgsql;

-- 함수: 출석률 계산
CREATE OR REPLACE FUNCTION calculate_attendance_rate(enrollment_id_param BIGINT)
RETURNS DECIMAL(5,2) AS $$
DECLARE
    total_classes INTEGER;
    present_count INTEGER;
    rate DECIMAL(5,2);
BEGIN
    SELECT COUNT(*)
    INTO total_classes
    FROM attendance
    WHERE enrollment_id = enrollment_id_param;
    
    IF total_classes = 0 THEN
        RETURN 100.00;
    END IF;
    
    SELECT COUNT(*)
    INTO present_count
    FROM attendance
    WHERE enrollment_id = enrollment_id_param
      AND status IN ('PRESENT', 'EXCUSED');
    
    rate := (present_count::DECIMAL / total_classes::DECIMAL) * 100;
    RETURN ROUND(rate, 2);
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 5. 트리거 함수 및 트리거
-- ============================================

-- 트리거 함수 1: 수강신청 승인 시 current_students 증가
CREATE OR REPLACE FUNCTION update_current_students_on_approval()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'APPROVED' AND (OLD.status IS NULL OR OLD.status != 'APPROVED') THEN
        UPDATE courses
        SET current_students = current_students + 1,
            updated_at = CURRENT_TIMESTAMP
        WHERE course_id = NEW.course_id;
        
        -- 정원이 가득 찼는지 확인
        UPDATE courses
        SET status = 'FULL'
        WHERE course_id = NEW.course_id
          AND current_students >= max_students;
    END IF;
    
    IF OLD.status = 'APPROVED' AND NEW.status != 'APPROVED' THEN
        UPDATE courses
        SET current_students = GREATEST(0, current_students - 1),
            updated_at = CURRENT_TIMESTAMP
        WHERE course_id = NEW.course_id;
        
        -- FULL 상태에서 벗어났는지 확인
        UPDATE courses
        SET status = 'OPEN'
        WHERE course_id = NEW.course_id
          AND status = 'FULL'
          AND current_students < max_students;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 1: 수강신청 상태 변경 시 정원 업데이트
DROP TRIGGER IF EXISTS trigger_update_current_students ON enrollments;
CREATE TRIGGER trigger_update_current_students
AFTER INSERT OR UPDATE ON enrollments
FOR EACH ROW
EXECUTE FUNCTION update_current_students_on_approval();

-- 트리거 함수 2: 수강신청 승인 시 총 학점 자동 업데이트
CREATE OR REPLACE FUNCTION update_student_total_credits()
RETURNS TRIGGER AS $$
BEGIN
    -- 승인된 경우 총 학점 업데이트
    IF NEW.status = 'APPROVED' AND (OLD.status IS NULL OR OLD.status != 'APPROVED') THEN
        UPDATE users
        SET total_credits = calculate_student_total_credits(NEW.student_id)
        WHERE user_id = NEW.student_id;
    END IF;
    
    -- 승인에서 다른 상태로 변경된 경우 총 학점 업데이트
    IF OLD.status = 'APPROVED' AND NEW.status != 'APPROVED' THEN
        UPDATE users
        SET total_credits = calculate_student_total_credits(NEW.student_id)
        WHERE user_id = NEW.student_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 2: 수강신청 상태 변경 시 총 학점 업데이트
DROP TRIGGER IF EXISTS trigger_update_student_total_credits ON enrollments;
CREATE TRIGGER trigger_update_student_total_credits
AFTER INSERT OR UPDATE ON enrollments
FOR EACH ROW
EXECUTE FUNCTION update_student_total_credits();

-- 트리거 함수 3: 성적 자동 계산
CREATE OR REPLACE FUNCTION calculate_grade_scores()
RETURNS TRIGGER AS $$
DECLARE
    total DECIMAL(5,2);
    letter VARCHAR(2);
    gpa_score DECIMAL(3,2);
BEGIN
    -- 총점 계산 (중간 30%, 기말 40%, 과제 20%, 출석 10%)
    total := (NEW.midterm_score * 0.3) + 
             (NEW.final_score * 0.4) + 
             (NEW.assignment_score * 0.2) + 
             (NEW.attendance_score * 0.1);
    
    NEW.total_score := ROUND(total, 2);
    
    -- 등급 및 평점 계산
    IF total >= 95 THEN
        letter := 'A+';
        gpa_score := 4.5;
    ELSIF total >= 90 THEN
        letter := 'A';
        gpa_score := 4.0;
    ELSIF total >= 85 THEN
        letter := 'B+';
        gpa_score := 3.5;
    ELSIF total >= 80 THEN
        letter := 'B';
        gpa_score := 3.0;
    ELSIF total >= 75 THEN
        letter := 'C+';
        gpa_score := 2.5;
    ELSIF total >= 70 THEN
        letter := 'C';
        gpa_score := 2.0;
    ELSIF total >= 65 THEN
        letter := 'D+';
        gpa_score := 1.5;
    ELSIF total >= 60 THEN
        letter := 'D';
        gpa_score := 1.0;
    ELSE
        letter := 'F';
        gpa_score := 0.0;
    END IF;
    
    NEW.letter_grade := letter;
    NEW.gpa := gpa_score;
    NEW.updated_at := CURRENT_TIMESTAMP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 3: 성적 입력/수정 시 자동 계산
DROP TRIGGER IF EXISTS trigger_calculate_grade_scores ON grades;
CREATE TRIGGER trigger_calculate_grade_scores
BEFORE INSERT OR UPDATE ON grades
FOR EACH ROW
EXECUTE FUNCTION calculate_grade_scores();

-- ============================================
-- 6. 샘플 데이터 삽입
-- ============================================

-- 사용자 샘플 데이터
INSERT INTO users (username, password, name, email, role, grade) VALUES
('student1', 'password1', '학생1', 'student1@example.com', 'STUDENT', 1),
('student2', 'password2', '학생2', 'student2@example.com', 'STUDENT', 2),
('instructor1', 'password1', '교수1', 'instructor1@example.com', 'INSTRUCTOR', NULL),
('instructor2', 'password2', '교수2', 'instructor2@example.com', 'INSTRUCTOR', NULL),
('admin', 'admin123', '관리자', 'admin@example.com', 'ADMIN', NULL)
ON CONFLICT (username) DO NOTHING;

-- 강의 샘플 데이터
INSERT INTO courses (course_code, course_name, description, instructor_id, max_students, credits, status) VALUES
('CS101', '컴퓨터 기초', '컴퓨터 과학의 기초 개념을 학습합니다.', 
 (SELECT user_id FROM users WHERE username = 'instructor1'), 30, 3, 'OPEN'),
('CS201', '자료구조', '다양한 자료구조와 알고리즘을 학습합니다.', 
 (SELECT user_id FROM users WHERE username = 'instructor1'), 25, 3, 'OPEN'),
('MATH101', '기초 수학', '대학 수학의 기초를 학습합니다.', 
 (SELECT user_id FROM users WHERE username = 'instructor2'), 40, 3, 'OPEN')
ON CONFLICT (course_code) DO NOTHING;

-- 기존 학생 데이터에 학년 및 총 학점 업데이트
UPDATE users SET grade = 1 WHERE role = 'STUDENT' AND grade IS NULL;
UPDATE users SET total_credits = calculate_student_total_credits(user_id) WHERE role = 'STUDENT';

-- ============================================
-- 완료 메시지
-- ============================================
DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'LMS 데이터베이스 스키마가 성공적으로 생성되었습니다!';
    RAISE NOTICE '============================================';
    RAISE NOTICE '포함된 기능:';
    RAISE NOTICE '  - 사용자 관리 (학생, 강사, 관리자)';
    RAISE NOTICE '  - 강의 관리';
    RAISE NOTICE '  - 수강신청 관리';
    RAISE NOTICE '  - 학점 시스템';
    RAISE NOTICE '  - 성적 관리';
    RAISE NOTICE '  - 출석 관리';
    RAISE NOTICE '  - 공지사항';
    RAISE NOTICE '  - 강의 자료 관리';
    RAISE NOTICE '  - 과제 관리';
    RAISE NOTICE '============================================';
END $$;
