package homework.librarymanagementsys250702.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费标准实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fee_standards")
public class FeeStandard {
    
    /**
     * 收费标准ID
     */
    @TableId(value = "fee_id", type = IdType.AUTO)
    private Long feeId;
    
    /**
     * 收费类型：1-借阅费，2-超期罚款，3-损坏赔偿，4-丢失赔偿
     */
    @TableField("fee_type")
    private Integer feeType;
    
    /**
     * 书籍分类（为空则表示通用）
     */
    @TableField("book_category")
    private String bookCategory;
    
    /**
     * 费用金额
     */
    @TableField("fee_amount")
    private BigDecimal feeAmount;
    
    /**
     * 计费单位：1-按天，2-按本，3-按次，4-固定金额
     */
    @TableField("billing_unit")
    private Integer billingUnit;
    
    /**
     * 费用描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    @TableField("is_active")
    private Integer isActive;
    
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