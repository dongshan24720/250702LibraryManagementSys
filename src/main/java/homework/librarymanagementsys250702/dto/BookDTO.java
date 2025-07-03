package homework.librarymanagementsys250702.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍DTO
 */
@Data
public class BookDTO {
    
    private Long bookId;
    
    @NotBlank(message = "ISBN不能为空")
    private String isbn;
    
    @NotBlank(message = "书名不能为空")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    private String author;
    
    private String publisher;
    
    private Integer publishYear;
    
    @NotBlank(message = "分类不能为空")
    private String category;
    
    private String description;
    
    @DecimalMin(value = "0.0", message = "价格不能为负数")
    private BigDecimal price;
    
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stockQuantity;
    
    @Min(value = 0, message = "可借数量不能为负数")
    private Integer availableQuantity;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 