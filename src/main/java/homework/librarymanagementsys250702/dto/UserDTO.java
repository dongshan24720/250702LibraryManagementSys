package homework.librarymanagementsys250702.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户DTO
 */
@Data
public class UserDTO {
    
    private Long userId;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    @Min(value = 0, message = "性别参数错误")
    @Max(value = 1, message = "性别参数错误")
    private Integer gender;
    
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄不能超过150")
    private Integer age;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String userType;
    
    private BigDecimal balance;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    /**
     * 从实体转换为DTO
     */
    public static UserDTO fromEntity(homework.librarymanagementsys250702.entity.User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRealName(user.getRealName());
        userDTO.setGender(user.getGender());
        userDTO.setAge(user.getAge());
        userDTO.setPhone(user.getPhone());
        userDTO.setEmail(user.getEmail());
        userDTO.setUserType(user.getUserType() == 1 ? "ADMIN" : "CUSTOMER");
        userDTO.setBalance(user.getBalance());
        userDTO.setStatus(user.getStatus());
        userDTO.setCreateTime(user.getCreateTime());
        userDTO.setUpdateTime(user.getUpdateTime());
        return userDTO;
    }
} 