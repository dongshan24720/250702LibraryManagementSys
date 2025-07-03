package homework.librarymanagementsys250702.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("books")
public class Book {
    
    /**
     * 书籍ID
     */
    @TableId(value = "book_id", type = IdType.AUTO)
    private Long bookId;
    
    /**
     * ISBN
     */
    @TableField("isbn")
    private String isbn;
    
    /**
     * 书名
     */
    @TableField("title")
    private String title;
    
    /**
     * 作者
     */
    @TableField("author")
    private String author;
    
    /**
     * 出版社
     */
    @TableField("publisher")
    private String publisher;
    
    /**
     * 出版年份
     */
    @TableField("publish_year")
    private Integer publishYear;
    
    /**
     * 分类
     */
    @TableField("category")
    private String category;
    
    /**
     * 书籍描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 书籍价值（用于损坏赔偿）
     */
    @TableField("price")
    private BigDecimal price;
    
    /**
     * 库存数量
     */
    @TableField("stock_quantity")
    private Integer stockQuantity;
    
    /**
     * 可借数量
     */
    @TableField("available_quantity")
    private Integer availableQuantity;
    
    /**
     * 书籍状态：0-正常，1-下架
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