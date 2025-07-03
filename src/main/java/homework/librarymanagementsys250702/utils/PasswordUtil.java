package homework.librarymanagementsys250702.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码工具类
 */
public class PasswordUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 密码加密
     */
    public static String encode(String password) {
        return passwordEncoder.encode(password);
    }
    
    /**
     * 密码验证
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 验证当前数据库中的哈希值问题
     */
    public static void verifyCurrentHashes() {
        // 数据库中当前的哈希值
        String currentAdminHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        
        // 生成正确的哈希值
        String correctAdminHash = encode("admin123");
        String correctCustomerHash = encode("123456");
        
        System.out.println("=== 密码验证报告 ===");
        System.out.println("当前数据库admin哈希: " + currentAdminHash);
        System.out.println("正确admin123哈希: " + correctAdminHash);
        System.out.println("正确123456哈希: " + correctCustomerHash);
        System.out.println();
        
        System.out.println("验证结果:");
        System.out.println("当前哈希 vs admin123: " + matches("admin123", currentAdminHash));
        System.out.println("当前哈希 vs 123456: " + matches("123456", currentAdminHash));
        System.out.println("正确admin哈希 vs admin123: " + matches("admin123", correctAdminHash));
        System.out.println("正确customer哈希 vs 123456: " + matches("123456", correctCustomerHash));
        
        System.out.println("\n需要更新的SQL:");
        System.out.println("UPDATE users SET password = '" + correctAdminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password = '" + correctCustomerHash + "' WHERE username IN ('customer1', 'customer2');");
    }
    
    /**
     * 测试方法：生成密码哈希
     */
    public static void main(String[] args) {
        verifyCurrentHashes();
    }
} 