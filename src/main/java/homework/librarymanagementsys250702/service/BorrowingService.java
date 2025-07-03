package homework.librarymanagementsys250702.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import homework.librarymanagementsys250702.dto.BorrowRequest;
import homework.librarymanagementsys250702.dto.BorrowingRecordDTO;
import homework.librarymanagementsys250702.dto.ReturnRequest;
import homework.librarymanagementsys250702.entity.Book;
import homework.librarymanagementsys250702.entity.BorrowingRecord;
import homework.librarymanagementsys250702.entity.FineRecord;
import homework.librarymanagementsys250702.entity.User;
import homework.librarymanagementsys250702.exception.BusinessException;
import homework.librarymanagementsys250702.repository.BookRepository;
import homework.librarymanagementsys250702.repository.BorrowingRecordRepository;
import homework.librarymanagementsys250702.repository.FineRecordRepository;
import homework.librarymanagementsys250702.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 借阅Service
 */
@Service
public class BorrowingService {
    
    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FeeStandardService feeStandardService;
    
    @Autowired
    private FineRecordRepository fineRecordRepository;
    
    /**
     * 借阅书籍
     */
    @Transactional
    public void borrowBook(Long userId, BorrowRequest borrowRequest) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        Book book = bookRepository.selectById(borrowRequest.getBookId());
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        
        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessException("书籍库存不足");
        }
        
        // 检查用户是否已借阅该书（只检查当前借阅状态的记录）
        List<BorrowingRecord> existingRecords = borrowingRecordRepository.findBorrowingByUserId(userId);
        boolean alreadyBorrowed = existingRecords.stream()
                .anyMatch(record -> record.getBookId().equals(borrowRequest.getBookId()) && record.getStatus() == 0);
        
        if (alreadyBorrowed) {
            throw new BusinessException("您已借阅该书籍，请先归还后再借阅");
        }
        
        // 创建借阅记录
        BorrowingRecord record = new BorrowingRecord();
        record.setUserId(userId);
        record.setBookId(borrowRequest.getBookId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(LocalDateTime.now().plusDays(borrowRequest.getBorrowDays()));
        record.setStatus(0); // 借阅中
        record.setBorrowingFee(BigDecimal.valueOf(2.0)); // 默认借阅费用
        
        borrowingRecordRepository.insert(record);
        
        // 更新书籍可借数量
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.updateById(book);
    }
    
    /**
     * 归还书籍（简单版本，保持向后兼容）
     */
    @Transactional
    public void returnBook(Long userId, Long recordId) {
        BorrowingRecord record = borrowingRecordRepository.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (record.getStatus() != 0) {
            throw new BusinessException("该书籍已归还");
        }
        
        // 更新借阅记录
        record.setReturnTime(LocalDateTime.now());
        record.setStatus(1); // 已归还
        
        // 计算超期天数
        if (record.getReturnTime().isAfter(record.getDueTime())) {
            long overdueDays = java.time.Duration.between(record.getDueTime(), record.getReturnTime()).toDays();
            record.setOverdueDays((int) overdueDays);
        }
        
        borrowingRecordRepository.updateById(record);
        
        // 恢复书籍可借数量
        Book book = bookRepository.selectById(record.getBookId());
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.updateById(book);
        
        // 从用户余额扣除费用
        User user = userRepository.selectById(userId);
        BigDecimal totalFee = record.getBorrowingFee();
        if (record.getOverdueDays() != null && record.getOverdueDays() > 0) {
            totalFee = totalFee.add(BigDecimal.valueOf(record.getOverdueDays() * 1.0)); // 超期费用
        }
        
        if (user.getBalance().compareTo(totalFee) < 0) {
            throw new BusinessException("余额不足，请充值后归还");
        }
        
        user.setBalance(user.getBalance().subtract(totalFee));
        userRepository.updateById(user);
    }
    
    /**
     * 归还书籍（支持不同归还类型）
     */
    @Transactional
    public void returnBookWithType(Long userId, Long recordId, ReturnRequest returnRequest) {
        BorrowingRecord record = borrowingRecordRepository.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (record.getStatus() != 0) {
            throw new BusinessException("该书籍已归还");
        }
        
        // 获取书籍信息用于费用计算
        Book book = bookRepository.selectById(record.getBookId());
        if (book == null) {
            throw new BusinessException("书籍信息不存在");
        }
        
        // 更新借阅记录基本信息
        record.setReturnTime(LocalDateTime.now());
        
        // 计算超期天数
        int overdueDays = 0;
        if (record.getReturnTime().isAfter(record.getDueTime())) {
            overdueDays = (int) java.time.Duration.between(record.getDueTime(), record.getReturnTime()).toDays();
            record.setOverdueDays(overdueDays);
        }
        
        // 设置备注
        if (returnRequest.getRemarks() != null && !returnRequest.getRemarks().trim().isEmpty()) {
            record.setRemarks(returnRequest.getRemarks());
        }
        if (returnRequest.getDamageDescription() != null && !returnRequest.getDamageDescription().trim().isEmpty()) {
            String currentRemarks = record.getRemarks() != null ? record.getRemarks() : "";
            record.setRemarks(currentRemarks + " [损坏描述: " + returnRequest.getDamageDescription() + "]");
        }
        
        // 根据归还类型处理费用和罚款记录
        BigDecimal baseBorrowingFee = record.getBorrowingFee() != null ? record.getBorrowingFee() : BigDecimal.ZERO;
        BigDecimal totalFee = baseBorrowingFee;
        boolean shouldRestoreBookQuantity = true;
        
        switch (returnRequest.getReturnType()) {
            case 1: // 正常归还
                record.setStatus(1);
                // 正常归还只收取基本借阅费
                break;
                
            case 2: // 超期归还
                record.setStatus(2);
                // 调整借阅费用为更高费率（超期情况下借阅费翻倍）
                BigDecimal overdueBaseFee = baseBorrowingFee.multiply(BigDecimal.valueOf(1.5));
                record.setBorrowingFee(overdueBaseFee);
                totalFee = overdueBaseFee;
                
                // 计算超期罚款
                int actualOverdueDays = Math.max(overdueDays, 1); // 至少按1天计算
                BigDecimal overdueFee = feeStandardService.calculateOverdueFee(book.getCategory(), actualOverdueDays);
                totalFee = totalFee.add(overdueFee);
                
                // 记录超期罚款到fine_records表
                FineRecord overdueFineRecord = new FineRecord();
                overdueFineRecord.setUserId(userId);
                overdueFineRecord.setRecordId(recordId);
                overdueFineRecord.setBookId(record.getBookId());
                overdueFineRecord.setFineType(1); // 1-超期罚款
                overdueFineRecord.setFineAmount(overdueFee);
                overdueFineRecord.setPaymentStatus(1); // 立即支付
                overdueFineRecord.setPaymentTime(LocalDateTime.now());
                overdueFineRecord.setReason("图书超期归还，超期天数：" + actualOverdueDays + "天");
                overdueFineRecord.setRemarks(returnRequest.getRemarks());
                fineRecordRepository.insert(overdueFineRecord);
                break;
                
            case 3: // 损坏归还
                record.setStatus(4);
                // 损坏情况下借阅费翻倍
                BigDecimal damageBaseFee = baseBorrowingFee.multiply(BigDecimal.valueOf(2.0));
                record.setBorrowingFee(damageBaseFee);
                totalFee = damageBaseFee;
                
                // 添加损坏赔偿费用
                BigDecimal damageFee = feeStandardService.getFeeStandard(3, book.getCategory()) != null 
                    ? feeStandardService.getFeeStandard(3, book.getCategory()).getFeeAmount()
                    : BigDecimal.valueOf(20.0);
                totalFee = totalFee.add(damageFee);
                
                // 记录损坏赔偿到fine_records表
                FineRecord damageFineRecord = new FineRecord();
                damageFineRecord.setUserId(userId);
                damageFineRecord.setRecordId(recordId);
                damageFineRecord.setBookId(record.getBookId());
                damageFineRecord.setFineType(2); // 2-损坏赔偿
                damageFineRecord.setFineAmount(damageFee);
                damageFineRecord.setPaymentStatus(1); // 立即支付
                damageFineRecord.setPaymentTime(LocalDateTime.now());
                damageFineRecord.setReason("图书损坏归还，需要赔偿");
                damageFineRecord.setRemarks(returnRequest.getDamageDescription());
                fineRecordRepository.insert(damageFineRecord);
                
                // 如果还有超期，也要加上超期费用
                if (overdueDays > 0) {
                    BigDecimal additionalOverdueFee = feeStandardService.calculateOverdueFee(book.getCategory(), overdueDays);
                    totalFee = totalFee.add(additionalOverdueFee);
                    
                    // 记录超期罚款
                    FineRecord additionalOverdueFineRecord = new FineRecord();
                    additionalOverdueFineRecord.setUserId(userId);
                    additionalOverdueFineRecord.setRecordId(recordId);
                    additionalOverdueFineRecord.setBookId(record.getBookId());
                    additionalOverdueFineRecord.setFineType(1); // 1-超期罚款
                    additionalOverdueFineRecord.setFineAmount(additionalOverdueFee);
                    additionalOverdueFineRecord.setPaymentStatus(1); // 立即支付
                    additionalOverdueFineRecord.setPaymentTime(LocalDateTime.now());
                    additionalOverdueFineRecord.setReason("图书损坏且超期归还，超期天数：" + overdueDays + "天");
                    additionalOverdueFineRecord.setRemarks("损坏+超期");
                    fineRecordRepository.insert(additionalOverdueFineRecord);
                }
                break;
                
            case 4: // 丢失归还
                record.setStatus(3);
                // 丢失情况下借阅费按最高费率（3倍）
                BigDecimal lostBaseFee = baseBorrowingFee.multiply(BigDecimal.valueOf(3.0));
                record.setBorrowingFee(lostBaseFee);
                totalFee = lostBaseFee;
                
                // 添加丢失赔偿费用
                BigDecimal lostFee = feeStandardService.getFeeStandard(4, book.getCategory()) != null 
                    ? feeStandardService.getFeeStandard(4, book.getCategory()).getFeeAmount()
                    : BigDecimal.valueOf(50.0);
                totalFee = totalFee.add(lostFee);
                
                // 记录丢失赔偿到fine_records表
                FineRecord lostFineRecord = new FineRecord();
                lostFineRecord.setUserId(userId);
                lostFineRecord.setRecordId(recordId);
                lostFineRecord.setBookId(record.getBookId());
                lostFineRecord.setFineType(3); // 3-丢失赔偿
                lostFineRecord.setFineAmount(lostFee);
                lostFineRecord.setPaymentStatus(1); // 立即支付
                lostFineRecord.setPaymentTime(LocalDateTime.now());
                lostFineRecord.setReason("图书丢失，需要赔偿");
                lostFineRecord.setRemarks(returnRequest.getRemarks());
                fineRecordRepository.insert(lostFineRecord);
                
                // 如果还有超期，也要加上超期费用
                if (overdueDays > 0) {
                    BigDecimal lostOverdueFee = feeStandardService.calculateOverdueFee(book.getCategory(), overdueDays);
                    totalFee = totalFee.add(lostOverdueFee);
                    
                    // 记录超期罚款
                    FineRecord lostOverdueFineRecord = new FineRecord();
                    lostOverdueFineRecord.setUserId(userId);
                    lostOverdueFineRecord.setRecordId(recordId);
                    lostOverdueFineRecord.setBookId(record.getBookId());
                    lostOverdueFineRecord.setFineType(1); // 1-超期罚款
                    lostOverdueFineRecord.setFineAmount(lostOverdueFee);
                    lostOverdueFineRecord.setPaymentStatus(1); // 立即支付
                    lostOverdueFineRecord.setPaymentTime(LocalDateTime.now());
                    lostOverdueFineRecord.setReason("图书丢失且超期归还，超期天数：" + overdueDays + "天");
                    lostOverdueFineRecord.setRemarks("丢失+超期");
                    fineRecordRepository.insert(lostOverdueFineRecord);
                }
                
                // 丢失的书不恢复库存
                shouldRestoreBookQuantity = false;
                break;
                
            default:
                throw new BusinessException("无效的归还类型");
        }
        
        // 更新借阅记录
        borrowingRecordRepository.updateById(record);
        
        // 恢复书籍可借数量（丢失情况除外）
        if (shouldRestoreBookQuantity) {
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookRepository.updateById(book);
        }
        
        // 从用户余额扣除费用
        User user = userRepository.selectById(userId);
        if (user.getBalance().compareTo(totalFee) < 0) {
            throw new BusinessException("余额不足，请充值后归还。需要支付: " + totalFee + "元，当前余额: " + user.getBalance() + "元");
        }
        
        user.setBalance(user.getBalance().subtract(totalFee));
        userRepository.updateById(user);
    }
    
    /**
     * 获取用户借阅记录
     */
    public List<BorrowingRecord> getUserBorrowingRecords(Long userId) {
        return borrowingRecordRepository.findBorrowingByUserId(userId);
    }
    
    /**
     * 获取用户借阅记录（包含书籍信息）
     */
    public List<BorrowingRecordDTO> getUserBorrowingRecordsWithBookInfo(Long userId) {
        // 获取用户的所有借阅记录，不仅仅是当前借阅的
        List<BorrowingRecord> records = borrowingRecordRepository.findAllBorrowingByUserId(userId);
        return records.stream().map(this::convertToBorrowingRecordDTO).toList();
    }
    
    /**
     * 转换为借阅记录DTO
     */
    private BorrowingRecordDTO convertToBorrowingRecordDTO(BorrowingRecord record) {
        BorrowingRecordDTO dto = new BorrowingRecordDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 设置ID字段（兼容前端）
        dto.setId(record.getRecordId());
        dto.setRecordId(record.getRecordId());
        
        // 设置日期字段（兼容前端）
        dto.setBorrowDate(record.getBorrowTime());
        dto.setDueDate(record.getDueTime());
        dto.setReturnDate(record.getReturnTime());
        
        // 获取书籍信息
        Book book = bookRepository.selectById(record.getBookId());
        if (book != null) {
            dto.setBookTitle(book.getTitle());
            dto.setBookAuthor(book.getAuthor());
            dto.setBookPublisher(book.getPublisher());
            dto.setBookCategory(book.getCategory());
        }
        
        // 转换状态
        dto.setStatus(convertStatusToString(record.getStatus()));
        
        // 计算总费用
        dto.setTotalFee(calculateTotalFee(record));
        
        return dto;
    }
    
    /**
     * 转换状态为字符串
     */
    private String convertStatusToString(Integer status) {
        if (status == null) return "UNKNOWN";
        return switch (status) {
            case 0 -> "BORROWED";
            case 1 -> "RETURNED";
            case 2 -> "OVERDUE";
            case 3 -> "LOST";
            case 4 -> "DAMAGED";
            default -> "UNKNOWN";
        };
    }
    
    /**
     * 计算总费用
     */
    private BigDecimal calculateTotalFee(BorrowingRecord record) {
        BigDecimal totalFee = record.getBorrowingFee() != null ? record.getBorrowingFee() : BigDecimal.ZERO;
        
        // 如果有超期天数，添加超期费用
        if (record.getOverdueDays() != null && record.getOverdueDays() > 0) {
            BigDecimal overdueFee = BigDecimal.valueOf(record.getOverdueDays() * 1.0);
            totalFee = totalFee.add(overdueFee);
        }
        
        return totalFee;
    }
    
    /**
     * 获取超期记录
     */
    public List<BorrowingRecord> getOverdueRecords() {
        return borrowingRecordRepository.findOverdueRecords();
    }
    
    /**
     * 获取借阅统计
     */
    public List<Object> getBorrowingStatistics() {
        return borrowingRecordRepository.countBorrowingByCategory();
    }
    
    /**
     * 获取用户当前借阅数量
     */
    public long getCurrentBorrowCount(Long userId) {
        return borrowingRecordRepository.countByUserIdAndStatusAndDeleted(userId, 0, 0);
    }
    
    /**
     * 获取用户总借阅数量
     */
    public long getTotalBorrowCount(Long userId) {
        return borrowingRecordRepository.countByUserIdAndDeleted(userId, 0);
    }
    
    /**
     * 获取用户逾期借阅数量
     */
    public long getOverdueBorrowCount(Long userId) {
        return borrowingRecordRepository.countByUserIdAndStatusAndDueDateBeforeAndDeleted(
            userId, 0, new Date(), 0);
    }
    
    /**
     * 获取系统中所有活跃借阅数量
     */
    public long getActiveBorrowsCount() {
        return borrowingRecordRepository.countByStatusAndDeleted(0, 0);
    }
    
    /**
     * 管理员获取所有借阅记录（分页）
     */
    public IPage<BorrowingRecordDTO> getBorrowingRecordsForAdmin(int current, int size, String username, String status, String startDate) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BorrowingRecord> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BorrowingRecord> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 根据用户名筛选
        if (org.springframework.util.StringUtils.hasText(username)) {
            // 先根据用户名查找用户ID
            User user = userRepository.findByUsername(username);
            if (user != null) {
                queryWrapper.eq("user_id", user.getUserId());
            } else {
                // 如果找不到用户，返回空结果
                queryWrapper.eq("user_id", -1);
            }
        }
        
        // 根据状态筛选
        if (org.springframework.util.StringUtils.hasText(status)) {
            Integer statusValue = convertStatusFromString(status);
            if (statusValue != null) {
                queryWrapper.eq("status", statusValue);
            }
        }
        
        // 根据开始日期筛选
        if (org.springframework.util.StringUtils.hasText(startDate)) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(startDate);
                queryWrapper.ge("borrow_time", date.atStartOfDay());
            } catch (Exception e) {
                // 日期格式错误，忽略此条件
            }
        }
        
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByDesc("borrow_time");
        
        IPage<BorrowingRecord> recordPage = borrowingRecordRepository.selectPage(page, queryWrapper);
        
        // 转换为DTO并添加用户名和书籍信息
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BorrowingRecordDTO> dtoPage = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        dtoPage.setTotal(recordPage.getTotal());
        
        java.util.List<BorrowingRecordDTO> dtoList = recordPage.getRecords().stream().map(record -> {
            BorrowingRecordDTO dto = convertToBorrowingRecordDTO(record);
            
            // 添加用户名
            User user = userRepository.selectById(record.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }
            
            return dto;
        }).toList();
        
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
    
    /**
     * 将状态字符串转换为数值
     */
    private Integer convertStatusFromString(String status) {
        return switch (status) {
            case "BORROWED" -> 0;
            case "RETURNED" -> 1;
            case "OVERDUE" -> 2;
            case "LOST" -> 3;
            case "DAMAGED" -> 4;
            default -> null;
        };
    }
} 