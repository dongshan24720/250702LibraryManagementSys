package homework.librarymanagementsys250702.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import homework.librarymanagementsys250702.entity.FeeStandard;
import homework.librarymanagementsys250702.repository.FeeStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收费标准服务类
 */
@Service
public class FeeStandardService {
    
    @Autowired
    private FeeStandardRepository feeStandardRepository;
    
    /**
     * 分页查询收费标准
     */
    public IPage<FeeStandard> getFeeStandardList(int current, int size, String keyword) {
        Page<FeeStandard> page = new Page<>(current, size);
        QueryWrapper<FeeStandard> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("description", keyword)
                .or().like("book_category", keyword)
            );
        }
        
        queryWrapper.orderByDesc("create_time");
        return feeStandardRepository.selectPage(page, queryWrapper);
    }
    
    /**
     * 获取所有启用的收费标准
     */
    public List<FeeStandard> getActiveFeeStandards() {
        QueryWrapper<FeeStandard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", 1);
        queryWrapper.orderByAsc("fee_type");
        return feeStandardRepository.selectList(queryWrapper);
    }
    
    /**
     * 根据类型和分类获取收费标准
     */
    public FeeStandard getFeeStandard(Integer feeType, String bookCategory) {
        QueryWrapper<FeeStandard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fee_type", feeType);
        queryWrapper.eq("is_active", 1);
        
        // 先查找特定分类的标准
        if (StringUtils.hasText(bookCategory)) {
            queryWrapper.eq("book_category", bookCategory);
            FeeStandard specific = feeStandardRepository.selectOne(queryWrapper);
            if (specific != null) {
                return specific;
            }
        }
        
        // 查找通用标准
        queryWrapper.clear();
        queryWrapper.eq("fee_type", feeType);
        queryWrapper.eq("is_active", 1);
        queryWrapper.and(wrapper -> wrapper.isNull("book_category").or().eq("book_category", ""));
        
        return feeStandardRepository.selectOne(queryWrapper);
    }
    
    /**
     * 添加收费标准
     */
    public void addFeeStandard(FeeStandard feeStandard) {
        feeStandardRepository.insert(feeStandard);
    }
    
    /**
     * 更新收费标准
     */
    public void updateFeeStandard(FeeStandard feeStandard) {
        feeStandardRepository.updateById(feeStandard);
    }
    
    /**
     * 删除收费标准
     */
    public void deleteFeeStandard(Long feeId) {
        feeStandardRepository.deleteById(feeId);
    }
    
    /**
     * 计算借阅费用
     */
    public BigDecimal calculateBorrowingFee(String bookCategory, int days) {
        FeeStandard standard = getFeeStandard(1, bookCategory); // 1-借阅费
        if (standard != null) {
            return standard.getFeeAmount().multiply(BigDecimal.valueOf(days));
        }
        return BigDecimal.valueOf(2.0 * days); // 默认每天2元
    }
    
    /**
     * 计算超期罚款
     */
    public BigDecimal calculateOverdueFee(String bookCategory, int overdueDays) {
        FeeStandard standard = getFeeStandard(2, bookCategory); // 2-超期罚款
        if (standard != null) {
            return standard.getFeeAmount().multiply(BigDecimal.valueOf(overdueDays));
        }
        return BigDecimal.valueOf(1.0 * overdueDays); // 默认每天1元罚款
    }
    
    /**
     * 初始化默认收费标准
     */
    public void initDefaultFeeStandards() {
        // 检查是否已有数据
        QueryWrapper<FeeStandard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", 1);
        List<FeeStandard> existing = feeStandardRepository.selectList(queryWrapper);
        if (!existing.isEmpty()) {
            return; // 已有数据，不需要初始化
        }
        
        // 初始化默认收费标准
        // 1. 通用借阅费：每天2元
        FeeStandard borrowFee = new FeeStandard();
        borrowFee.setFeeType(1);
        borrowFee.setBookCategory(null);
        borrowFee.setFeeAmount(BigDecimal.valueOf(2.0));
        borrowFee.setBillingUnit(1);
        borrowFee.setDescription("通用图书借阅费，每天2元");
        borrowFee.setIsActive(1);
        feeStandardRepository.insert(borrowFee);
        
        // 2. 通用超期罚款：每天1元
        FeeStandard lateFee = new FeeStandard();
        lateFee.setFeeType(2);
        lateFee.setBookCategory(null);
        lateFee.setFeeAmount(BigDecimal.valueOf(1.0));
        lateFee.setBillingUnit(1);
        lateFee.setDescription("通用图书超期罚款，每天1元");
        lateFee.setIsActive(1);
        feeStandardRepository.insert(lateFee);
        
        // 3. 通用损坏赔偿：按书籍价值的50%
        FeeStandard damageFee = new FeeStandard();
        damageFee.setFeeType(3);
        damageFee.setBookCategory(null);
        damageFee.setFeeAmount(BigDecimal.valueOf(20.0));
        damageFee.setBillingUnit(4);
        damageFee.setDescription("通用图书损坏赔偿，固定20元");
        damageFee.setIsActive(1);
        feeStandardRepository.insert(damageFee);
        
        // 4. 通用丢失赔偿：按书籍价值的100%
        FeeStandard lostFee = new FeeStandard();
        lostFee.setFeeType(4);
        lostFee.setBookCategory(null);
        lostFee.setFeeAmount(BigDecimal.valueOf(50.0));
        lostFee.setBillingUnit(4);
        lostFee.setDescription("通用图书丢失赔偿，固定50元");
        lostFee.setIsActive(1);
        feeStandardRepository.insert(lostFee);
    }
} 