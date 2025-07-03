package homework.librarymanagementsys250702.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import homework.librarymanagementsys250702.dto.LoginRequest;
import homework.librarymanagementsys250702.dto.UserDTO;
import homework.librarymanagementsys250702.entity.User;
import homework.librarymanagementsys250702.exception.BusinessException;
import homework.librarymanagementsys250702.repository.UserRepository;
import homework.librarymanagementsys250702.utils.JwtUtil;
import homework.librarymanagementsys250702.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户Service
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!PasswordUtil.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() != 0) {
            throw new BusinessException("账户已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId(), user.getUserType());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToDTO(user));
        
        return result;
    }
    
    /**
     * 用户登录（重载方法）
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() != 0) {
            throw new BusinessException("账户已被禁用");
        }
        
        return user;
    }
    
    /**
     * 用户注册
     */
    public void register(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(PasswordUtil.encode("123456")); // 默认密码
        
        // 根据前端传入的用户类型设置
        if (userDTO.getUserType() != null) {
            user.setUserType("ADMIN".equals(userDTO.getUserType()) ? 1 : 0);
        } else {
            user.setUserType(0); // 默认为客户
        }
        
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(0);
        
        userRepository.insert(user);
    }
    
    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        user.setPassword(PasswordUtil.encode(newPassword));
        userRepository.updateById(user);
    }
    
    /**
     * 充值
     */
    public void recharge(Long userId, BigDecimal amount) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setBalance(user.getBalance().add(amount));
        userRepository.updateById(user);
    }
    
    /**
     * 更新用户余额
     */
    public void updateUserBalance(Long userId, BigDecimal balance) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("余额不能为负数");
        }

        user.setBalance(balance);
        userRepository.updateById(user);
    }
    
    /**
     * 重置用户密码（用于修复密码问题）
     */
    public void resetUserPassword(String username, String encodedPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在: " + username);
        }
        
        user.setPassword(encodedPassword);
        userRepository.updateById(user);
    }
    
    /**
     * 分页查询用户
     */
    public IPage<UserDTO> getUserList(int current, int size, String keyword) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("username", keyword)
                    .or().like("real_name", keyword)
                    .or().like("phone", keyword);
        }
        
        IPage<User> userPage = userRepository.selectPage(page, queryWrapper);
        
        Page<UserDTO> resultPage = new Page<>(current, size);
        resultPage.setTotal(userPage.getTotal());
        resultPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToDTO)
                .toList());
        
        return resultPage;
    }
    
    /**
     * 获取用户个人信息
     */
    public Map<String, Object> getUserProfile(Long userId) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", user.getUserId());
        profile.put("username", user.getUsername());
        profile.put("realName", user.getRealName());
        profile.put("gender", user.getGender());
        profile.put("age", user.getAge());
        profile.put("phone", user.getPhone());
        profile.put("email", user.getEmail());
        profile.put("balance", user.getBalance());
        profile.put("userType", user.getUserType() == 1 ? "ADMIN" : "CUSTOMER");
        profile.put("createdAt", user.getCreateTime());
        profile.put("updatedAt", user.getUpdateTime());
        
        return profile;
    }
    
    /**
     * 更新用户个人信息
     */
    public void updateUserProfile(Long userId, Map<String, Object> profileData) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新允许修改的字段
        if (profileData.containsKey("realName")) {
            user.setRealName((String) profileData.get("realName"));
        }
        if (profileData.containsKey("gender")) {
            user.setGender(Integer.valueOf(profileData.get("gender").toString()));
        }
        if (profileData.containsKey("age")) {
            user.setAge(Integer.valueOf(profileData.get("age").toString()));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone((String) profileData.get("phone"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail((String) profileData.get("email"));
        }
        
        userRepository.updateById(user);
    }
    
    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        // 转换userType为字符串格式
        userDTO.setUserType(user.getUserType() == 1 ? "ADMIN" : "CUSTOMER");
        return userDTO;
    }
    
    /**
     * 管理员更新用户信息
     */
    public void updateUserByAdmin(UserDTO userDTO) {
        User user = userRepository.selectById(userDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户信息
        if (userDTO.getUsername() != null) {
            // 检查用户名是否已存在（排除当前用户）
            User existingUser = userRepository.findByUsername(userDTO.getUsername());
            if (existingUser != null && !existingUser.getUserId().equals(userDTO.getUserId())) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getRealName() != null) {
            user.setRealName(userDTO.getRealName());
        }
        if (userDTO.getGender() != null) {
            user.setGender(userDTO.getGender());
        }
        if (userDTO.getAge() != null) {
            user.setAge(userDTO.getAge());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getUserType() != null) {
            user.setUserType("ADMIN".equals(userDTO.getUserType()) ? 1 : 0);
        }
        if (userDTO.getBalance() != null) {
            user.setBalance(userDTO.getBalance());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        
        userRepository.updateById(user);
    }
    
    /**
     * 管理员删除用户
     */
    public void deleteUserByAdmin(Long userId) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户是否有未归还的借阅记录
        // 这里可以添加相关检查逻辑
        
        userRepository.deleteById(userId);
    }
} 