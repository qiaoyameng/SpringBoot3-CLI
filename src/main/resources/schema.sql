CREATE DATABASE IF NOT EXISTS volunserve DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE volunserve;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    real_name VARCHAR(50),
    id_card VARCHAR(18),
    role VARCHAR(20) DEFAULT 'VOLUNTEER',
    avatar_url VARCHAR(500),
    total_service_hours INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'RECRUITING',
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    location VARCHAR(300) NOT NULL,
    address VARCHAR(500),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    required_volunteers INT NOT NULL,
    current_volunteers INT DEFAULT 0,
    requirements TEXT,
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    organizer_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_organizer (organizer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    apply_reason TEXT,
    reviewer_id BIGINT,
    review_note TEXT,
    reviewed_at DATETIME,
    checked_in_at DATETIME,
    checked_out_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    INDEX idx_activity (activity_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_checked_in (checked_in_at),
    INDEX idx_checked_out (checked_out_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS service_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL,
    service_hours DECIMAL(5, 2),
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    certificate_number VARCHAR(100),
    certificate_generated_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_registration (registration_id),
    INDEX idx_activity (activity_id),
    INDEX idx_user (user_id),
    INDEX idx_certificate (certificate_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS forum_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT,
    user_id BIGINT NOT NULL,
    title VARCHAR(300) NOT NULL,
    content TEXT,
    is_experience BOOLEAN DEFAULT FALSE,
    is_photo_wall BOOLEAN DEFAULT FALSE,
    like_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_activity (activity_id),
    INDEX idx_user (user_id),
    INDEX idx_experience (is_experience),
    INDEX idx_photo_wall (is_photo_wall),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS forum_photos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    photo_url VARCHAR(500) NOT NULL,
    description VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_post (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS forum_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    like_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_post (post_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    activity_id BIGINT,
    registration_id BIGINT,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200),
    content TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    read_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_read (is_read),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (username, password, email, phone, real_name, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@volunserve.com', '13800138000', '系统管理员', 'ADMIN'),
('volunteer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'volunteer1@example.com', '13800138001', '张三', 'VOLUNTEER'),
('volunteer2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'volunteer2@example.com', '13800138002', '李四', 'VOLUNTEER');

INSERT INTO activities (title, description, category, status, start_time, end_time, location, required_volunteers, contact_name, contact_phone) VALUES
('社区环保清洁行动', '清理社区公园垃圾，宣传环保知识', 'ENVIRONMENTAL', 'RECRUITING', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY + INTERVAL 4 HOUR), '阳光社区公园', 20, '王主任', '13900139001'),
('敬老院探访活动', '为孤寡老人提供陪伴和生活帮助', 'ELDERLY_ASSISTANCE', 'RECRUITING', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY + INTERVAL 3 HOUR), '幸福敬老院', 15, '李院长', '13900139002'),
('支教辅导课程', '为贫困地区儿童提供学习辅导', 'EDUCATION', 'RECRUITING', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY + INTERVAL 6 HOUR), '希望小学', 10, '张校长', '13900139003'),
('社区义诊活动', '为居民提供免费健康检查和咨询', 'MEDICAL', 'RECRUITING', DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY + INTERVAL 5 HOUR), '社区服务中心', 8, '刘医生', '13900139004');
