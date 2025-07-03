package homework.librarymanagementsys250702.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录DTO，包含书籍信息
 */
@Data
public class BorrowingRecordDTO {
    /**
     * 借阅记录ID
     */
    private Long id;
    
    /**
     * 借阅记录ID（与id相同，为了兼容前端）
     */
    private Long recordId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 书籍ID
     */
    private Long bookId;
    
    /**
     * 书名
     */
    private String bookTitle;
    
    /**
     * 作者
     */
    private String bookAuthor;
    
    /**
     * 出版社
     */
    private String bookPublisher;
    
    /**
     * 分类
     */
    private String bookCategory;
    
    /**
     * 借阅时间
     */
    private LocalDateTime borrowTime;
    
    /**
     * 借阅日期（为了兼容前端）
     */
    private LocalDateTime borrowDate;
    
    /**
     * 应还时间
     */
    private LocalDateTime dueTime;
    
    /**
     * 应还日期（为了兼容前端）
     */
    private LocalDateTime dueDate;
    
    /**
     * 实际归还时间
     */
    private LocalDateTime returnTime;
    
    /**
     * 归还日期（为了兼容前端）
     */
    private LocalDateTime returnDate;
    
    /**
     * 借阅状态
     */
    private String status;
    
    /**
     * 借阅费用
     */
    private BigDecimal borrowingFee;
    
    /**
     * 总费用（为了兼容前端）
     */
    private BigDecimal totalFee;
    
    /**
     * 超期天数
     */
    private Integer overdueDays;
    
    /**
     * 备注
     */
    private String remarks;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 