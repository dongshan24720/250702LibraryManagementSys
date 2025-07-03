package homework.librarymanagementsys250702.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import homework.librarymanagementsys250702.dto.Result;
import homework.librarymanagementsys250702.entity.FeeStandard;
import homework.librarymanagementsys250702.service.FeeStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 收费标准管理控制器
 */
@RestController
@RequestMapping("/admin/fee-standards")
public class FeeStandardController {
    
    @Autowired
    private FeeStandardService feeStandardService;
    
    /**
     * 查询收费标准列表
     */
    @GetMapping
    public Result<IPage<FeeStandard>> getFeeStandardList(@RequestParam(defaultValue = "1") int current,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(required = false) String keyword) {
        IPage<FeeStandard> feeStandards = feeStandardService.getFeeStandardList(current, size, keyword);
        return Result.success(feeStandards);
    }
    
    /**
     * 添加收费标准
     */
    @PostMapping
    public Result<String> addFeeStandard(@Valid @RequestBody FeeStandard feeStandard) {
        feeStandardService.addFeeStandard(feeStandard);
        return Result.success("收费标准添加成功");
    }
    
    /**
     * 更新收费标准
     */
    @PutMapping("/{feeId}")
    public Result<String> updateFeeStandard(@PathVariable Long feeId, @Valid @RequestBody FeeStandard feeStandard) {
        feeStandard.setFeeId(feeId);
        feeStandardService.updateFeeStandard(feeStandard);
        return Result.success("收费标准更新成功");
    }
    
    /**
     * 删除收费标准
     */
    @DeleteMapping("/{feeId}")
    public Result<String> deleteFeeStandard(@PathVariable Long feeId) {
        feeStandardService.deleteFeeStandard(feeId);
        return Result.success("收费标准删除成功");
    }
    
    /**
     * 初始化默认收费标准
     */
    @PostMapping("/init")
    public Result<String> initFeeStandards() {
        feeStandardService.initDefaultFeeStandards();
        return Result.success("默认收费标准初始化成功");
    }
} 