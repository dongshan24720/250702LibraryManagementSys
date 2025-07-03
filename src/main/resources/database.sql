-- 书店租书管理系统数据库SQL文件
-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_management;

-- 1. 用户表（包含客户和管理员）
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-女，1-男',
    age INT COMMENT '年龄',
    phone VARCHAR(11) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    user_type TINYINT DEFAULT 0 COMMENT '用户类型：0-客户，1-管理员',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    status TINYINT DEFAULT 0 COMMENT '用户状态：0-正常，1-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='用户表';

-- 2. 书籍表
CREATE TABLE books (
    book_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '书籍ID',
    isbn VARCHAR(20) UNIQUE COMMENT 'ISBN',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_year INT COMMENT '出版年份',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    description TEXT COMMENT '书籍描述',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '书籍价值',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    available_quantity INT DEFAULT 0 COMMENT '可借数量',
    status TINYINT DEFAULT 0 COMMENT '书籍状态：0-正常，1-下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_isbn (isbn),
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='书籍表';

-- 3. 借阅记录表
CREATE TABLE borrowing_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '借阅记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    borrow_time DATETIME NOT NULL COMMENT '借阅时间',
    due_time DATETIME NOT NULL COMMENT '应还时间',
    return_time DATETIME COMMENT '实际归还时间',
    status TINYINT DEFAULT 0 COMMENT '借阅状态：0-借阅中，1-已归还，2-超期，3-丢失，4-损坏',
    borrowing_fee DECIMAL(8,2) DEFAULT 0.00 COMMENT '借阅费用',
    overdue_days INT DEFAULT 0 COMMENT '超期天数',
    remarks TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_time (borrow_time),
    INDEX idx_due_time (due_time),
    CONSTRAINT fk_borrowing_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_borrowing_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='借阅记录表';

-- 4. 收费标准表
CREATE TABLE fee_standards (
    fee_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收费标准ID',
    fee_type TINYINT NOT NULL COMMENT '收费类型：1-借阅费，2-超期罚款，3-损坏赔偿，4-丢失赔偿',
    book_category VARCHAR(50) COMMENT '书籍分类（为空则表示通用）',
    fee_amount DECIMAL(8,2) NOT NULL COMMENT '费用金额',
    billing_unit TINYINT DEFAULT 1 COMMENT '计费单位：1-按天，2-按本，3-按次，4-固定金额',
    description VARCHAR(200) COMMENT '费用描述',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_fee_type (fee_type),
    INDEX idx_book_category (book_category),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB COMMENT='收费标准表';

-- 5. 罚款记录表
CREATE TABLE fine_records (
    fine_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '罚款记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_id BIGINT COMMENT '借阅记录ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    fine_type TINYINT NOT NULL COMMENT '罚款类型：1-超期罚款，2-损坏赔偿，3-丢失赔偿',
    fine_amount DECIMAL(8,2) NOT NULL COMMENT '罚款金额',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付',
    payment_time DATETIME COMMENT '支付时间',
    reason VARCHAR(500) COMMENT '罚款原因',
    remarks TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_record_id (record_id),
    INDEX idx_book_id (book_id),
    INDEX idx_fine_type (fine_type),
    INDEX idx_payment_status (payment_status),
    CONSTRAINT fk_fine_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_fine_record FOREIGN KEY (record_id) REFERENCES borrowing_records(record_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_fine_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='罚款记录表';

-- 创建触发器：自动更新书籍可借数量
DELIMITER $$

-- 借阅时减少可借数量
CREATE TRIGGER tr_borrowing_insert
AFTER INSERT ON borrowing_records
FOR EACH ROW
BEGIN
    IF NEW.status = 0 THEN  -- 借阅中
        UPDATE books 
        SET available_quantity = available_quantity - 1 
        WHERE book_id = NEW.book_id AND available_quantity > 0;
    END IF;
END$$

-- 归还时增加可借数量
CREATE TRIGGER tr_borrowing_update
AFTER UPDATE ON borrowing_records
FOR EACH ROW
BEGIN
    -- 从借阅中变为已归还
    IF OLD.status = 0 AND NEW.status = 1 THEN
        UPDATE books 
        SET available_quantity = available_quantity + 1 
        WHERE book_id = NEW.book_id;
    END IF;
    
    -- 自动计算超期天数
    IF NEW.return_time IS NOT NULL AND NEW.return_time > NEW.due_time THEN
        UPDATE borrowing_records 
        SET overdue_days = DATEDIFF(NEW.return_time, NEW.due_time)
        WHERE record_id = NEW.record_id;
    END IF;
END$$

-- 自动生成超期罚款记录
CREATE TRIGGER tr_overdue_fine
AFTER UPDATE ON borrowing_records
FOR EACH ROW
BEGIN
    IF OLD.status = 0 AND NEW.status = 1 AND NEW.overdue_days > 0 THEN
        INSERT INTO fine_records (user_id, record_id, book_id, fine_type, fine_amount, reason)
        VALUES (NEW.user_id, NEW.record_id, NEW.book_id, 1, NEW.overdue_days * 1.0, 
                CONCAT('超期', NEW.overdue_days, '天，每天罚款1元'));
    END IF;
END$$

DELIMITER ;

-- 插入默认收费标准数据
INSERT INTO fee_standards (fee_type, book_category, fee_amount, billing_unit, description) VALUES
(1, NULL, 2.00, 2, '通用借阅费：每本2元'),
(1, '小说', 1.50, 2, '小说类借阅费：每本1.5元'),
(1, '教材', 3.00, 2, '教材类借阅费：每本3元'),
(2, NULL, 1.00, 1, '通用超期罚款：每天1元'),
(3, NULL, 50.00, 4, '书籍损坏赔偿：固定50元'),
(4, NULL, 100.00, 4, '书籍丢失赔偿：按书籍价值100%赔偿');

-- 插入默认管理员账户
INSERT INTO users (username, password, real_name, gender, age, phone, email, user_type, balance, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyoyNKRRbBm8qcAQWrqhzEHQgwG', '系统管理员', 1, 30, '13800138000', 'admin@library.com', 1, 0.00, 0);
-- 密码为: admin123

-- 插入测试用户
INSERT INTO users (username, password, real_name, gender, age, phone, email, user_type, balance, status) VALUES
('customer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyoyNKRRbBm8qcAQWrqhzEHQgwG', '张三', 1, 25, '13800138001', 'customer1@library.com', 0, 100.00, 0),
('customer2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyoyNKRRbBm8qcAQWrqhzEHQgwG', '李四', 0, 28, '13800138002', 'customer2@library.com', 0, 50.00, 0);
-- 密码为: 123456

-- 插入测试书籍数据
INSERT INTO books (isbn, title, author, publisher, publish_year, category, description, price, stock_quantity, available_quantity) VALUES
('978-7-111-40815-5', 'Java核心技术', 'Cay S. Horstmann', '机械工业出版社', 2020, '计算机', 'Java编程经典教材', 89.00, 10, 10),
('978-7-302-50947-8', '数据结构与算法分析', 'Mark Allen Weiss', '清华大学出版社', 2019, '计算机', '算法学习必备', 65.00, 8, 8),
('978-7-201-15707-9', '百年孤独', '加西亚·马尔克斯', '人民文学出版社', 2017, '小说', '魔幻现实主义经典', 39.50, 15, 15),
('978-7-508-69548-2', '活着', '余华', '中信出版社', 2018, '小说', '中国当代文学经典', 28.00, 12, 12),
('978-7-111-58022-9', 'Spring Boot实战', 'Craig Walls', '机械工业出版社', 2021, '计算机', 'Spring Boot开发指南', 79.00, 6, 6);

-- 创建视图：用户借阅统计
CREATE VIEW v_user_borrowing_stats AS
SELECT 
    u.user_id,
    u.username,
    u.real_name,
    COUNT(br.record_id) AS total_borrowed,
    COUNT(CASE WHEN br.status = 0 THEN 1 END) AS currently_borrowed,
    COUNT(CASE WHEN br.status = 2 THEN 1 END) AS overdue_count,
    COALESCE(SUM(fr.fine_amount), 0) AS total_fine_amount
FROM users u
LEFT JOIN borrowing_records br ON u.user_id = br.user_id AND br.deleted = 0
LEFT JOIN fine_records fr ON u.user_id = fr.user_id AND fr.deleted = 0
WHERE u.deleted = 0
GROUP BY u.user_id, u.username, u.real_name;

-- 创建视图：书籍借阅统计
CREATE VIEW v_book_borrowing_stats AS
SELECT 
    b.book_id,
    b.title,
    b.author,
    b.category,
    b.stock_quantity,
    b.available_quantity,
    COUNT(br.record_id) AS total_borrowed,
    COUNT(CASE WHEN br.status = 0 THEN 1 END) AS currently_borrowed,
    COALESCE(AVG(DATEDIFF(br.return_time, br.borrow_time)), 0) AS avg_borrow_days
FROM books b
LEFT JOIN borrowing_records br ON b.book_id = br.book_id AND br.deleted = 0
WHERE b.deleted = 0
GROUP BY b.book_id, b.title, b.author, b.category, b.stock_quantity, b.available_quantity;

-- 创建存储过程：批量导入用户
DELIMITER $$
CREATE PROCEDURE sp_batch_import_users(
    IN p_user_data JSON
)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE user_count INT;
    DECLARE v_username VARCHAR(50);
    DECLARE v_real_name VARCHAR(100);
    DECLARE v_gender TINYINT;
    DECLARE v_age INT;
    DECLARE v_phone VARCHAR(11);
    DECLARE v_email VARCHAR(100);
    
    SET user_count = JSON_LENGTH(p_user_data);
    
    WHILE i < user_count DO
        SET v_username = JSON_UNQUOTE(JSON_EXTRACT(p_user_data, CONCAT('$[', i, '].username')));
        SET v_real_name = JSON_UNQUOTE(JSON_EXTRACT(p_user_data, CONCAT('$[', i, '].realName')));
        SET v_gender = JSON_EXTRACT(p_user_data, CONCAT('$[', i, '].gender'));
        SET v_age = JSON_EXTRACT(p_user_data, CONCAT('$[', i, '].age'));
        SET v_phone = JSON_UNQUOTE(JSON_EXTRACT(p_user_data, CONCAT('$[', i, '].phone')));
        SET v_email = JSON_UNQUOTE(JSON_EXTRACT(p_user_data, CONCAT('$[', i, '].email')));
        
        INSERT IGNORE INTO users (username, password, real_name, gender, age, phone, email, user_type, balance, status)
        VALUES (v_username, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyoyNKRRbBm8qcAQWrqhzEHQgwG', 
                v_real_name, v_gender, v_age, v_phone, v_email, 0, 0.00, 0);
        
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

-- 创建定时任务相关表（用于处理超期检查）
CREATE EVENT IF NOT EXISTS ev_check_overdue
ON SCHEDULE EVERY 1 HOUR
DO
UPDATE borrowing_records 
SET status = 2 
WHERE status = 0 
AND due_time < NOW() 
AND deleted = 0; 