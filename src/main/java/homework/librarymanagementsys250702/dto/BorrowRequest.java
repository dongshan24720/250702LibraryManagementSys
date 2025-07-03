package homework.librarymanagementsys250702.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 借阅请求DTO
 */
@Data
public class BorrowRequest {
    
    @NotNull(message = "书籍ID不能为空")
    private Long bookId;
    
    private Integer borrowDays = 30; // 默认借阅30天
} 