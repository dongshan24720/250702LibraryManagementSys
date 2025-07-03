package homework.librarymanagementsys250702.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类（包含客户和管理员）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {
    
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    
    /**
     * 密码
     */
    @TableField("password")
    private String password;
    
    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;
    
    /**
     * 性别：0-女，1-男
     */
    @TableField("gender")
    private Integer gender;
    
    /**
     * 年龄
     */
    @TableField("age")
    private Integer age;
    
    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;
    
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    
    /**
     * 用户类型：0-客户，1-管理员
     */
    @TableField("user_type")
    private Integer userType;
    
    /**
     * 账户余额
     */
    @TableField("balance")
    private BigDecimal balance;
    
    /**
     * 用户状态：0-正常，1-禁用
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除字段：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 