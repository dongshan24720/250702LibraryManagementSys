package homework.librarymanagementsys250702.controller;

import homework.librarymanagementsys250702.dto.LoginRequest;
import homework.librarymanagementsys250702.dto.Result;
import homework.librarymanagementsys250702.dto.UserDTO;
import homework.librarymanagementsys250702.entity.User;
import homework.librarymanagementsys250702.service.UserService;
import homework.librarymanagementsys250702.utils.JwtUtil;
import homework.librarymanagementsys250702.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId(), user.getUserType());
        
        // 构造返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", UserDTO.fromEntity(user));
        
        return Result.success(data);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return Result.success("注册成功");
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // JWT是无状态的，登出只需要前端删除token即可
        return Result.success("登出成功");
    }
    
    /**
     * 临时端点：修复密码问题
     */
    @GetMapping("/fix-passwords")
    public Result<Map<String, String>> fixPasswords() {
        String admin123Hash = PasswordUtil.encode("admin123");
        String customer123Hash = PasswordUtil.encode("123456");
        
        // 验证当前数据库中的哈希
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyoyNKRRbBm8qcAQWrqhzEHQgwG";
        
        Map<String, String> result = new HashMap<>();
        result.put("admin123_hash", admin123Hash);
        result.put("customer123_hash", customer123Hash);
        result.put("admin123_验证", String.valueOf(PasswordUtil.matches("admin123", admin123Hash)));
        result.put("customer123_验证", String.valueOf(PasswordUtil.matches("123456", customer123Hash)));
        result.put("当前哈希与admin123匹配", String.valueOf(PasswordUtil.matches("admin123", currentHash)));
        result.put("当前哈希与123456匹配", String.valueOf(PasswordUtil.matches("123456", currentHash)));
        
        // 更新数据库密码
        userService.resetUserPassword("admin", admin123Hash);
        userService.resetUserPassword("customer1", customer123Hash);
        userService.resetUserPassword("customer2", customer123Hash);
        
        result.put("状态", "密码已重置");
        
        return Result.success("密码修复完成", result);
    }
} 