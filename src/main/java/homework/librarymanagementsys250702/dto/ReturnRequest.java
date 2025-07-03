package homework.librarymanagementsys250702.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 归还请求DTO
 */
@Data
public class ReturnRequest {
    
    /**
     * 归还类型：1-正常归还，2-超期归还，3-损坏归还，4-丢失归还
     */
    @NotNull(message = "归还类型不能为空")
    private Integer returnType;
    
    /**
     * 备注说明
     */
    private String remarks;
    
    /**
     * 损坏程度描述（损坏归还时使用）
     */
    private String damageDescription;
} 