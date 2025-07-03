package homework.librarymanagementsys250702.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 罚款记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fine_records")
public class FineRecord {
    
    /**
     * 罚款记录ID
     */
    @TableId(value = "fine_id", type = IdType.AUTO)
    private Long fineId;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 借阅记录ID
     */
    @TableField("record_id")
    private Long recordId;
    
    /**
     * 书籍ID
     */
    @TableField("book_id")
    private Long bookId;
    
    /**
     * 罚款类型：1-超期罚款，2-损坏赔偿，3-丢失赔偿
     */
    @TableField("fine_type")
    private Integer fineType;
    
    /**
     * 罚款金额
     */
    @TableField("fine_amount")
    private BigDecimal fineAmount;
    
    /**
     * 支付状态：0-未支付，1-已支付
     */
    @TableField("payment_status")
    private Integer paymentStatus;
    
    /**
     * 支付时间
     */
    @TableField("payment_time")
    private LocalDateTime paymentTime;
    
    /**
     * 罚款原因
     */
    @TableField("reason")
    private String reason;
    
    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
    
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