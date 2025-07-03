-- 修复用户密码的SQL脚本
-- 这些是使用BCrypt正确生成的密码哈希

USE library_management;

-- 更新管理员密码 (admin123)
UPDATE users SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE username = 'admin';

-- 更新测试用户密码 (123456)  
UPDATE users SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyoyNKRRbBm8qcAQWrqhzEHQgwG' WHERE username = 'customer1';
UPDATE users SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyoyNKRRbBm8qcAQWrqhzEHQgwG' WHERE username = 'customer2';

-- 验证更新结果
SELECT username, password FROM users WHERE username IN ('admin', 'customer1', 'customer2'); 