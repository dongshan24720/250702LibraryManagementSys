package homework.librarymanagementsys250702.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("borrowing_records")
public class BorrowingRecord {
    
    /**
     * 借阅记录ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 书籍ID
     */
    @TableField("book_id")
    private Long bookId;
    
    /**
     * 借阅时间
     */
    @TableField("borrow_time")
    private LocalDateTime borrowTime;
    
    /**
     * 应还时间
     */
    @TableField("due_time")
    private LocalDateTime dueTime;
    
    /**
     * 实际归还时间
     */
    @TableField("return_time")
    private LocalDateTime returnTime;
    
    /**
     * 借阅状态：0-借阅中，1-已归还，2-超期，3-丢失，4-损坏
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 借阅费用
     */
    @TableField("borrowing_fee")
    private BigDecimal borrowingFee;
    
    /**
     * 超期天数
     */
    @TableField("overdue_days")
    private Integer overdueDays;
    
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