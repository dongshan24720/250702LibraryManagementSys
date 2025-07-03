package homework.librarymanagementsys250702.controller;

import homework.librarymanagementsys250702.dto.BookDTO;
import homework.librarymanagementsys250702.dto.BorrowRequest;
import homework.librarymanagementsys250702.dto.BorrowingRecordDTO;
import homework.librarymanagementsys250702.dto.Result;
import homework.librarymanagementsys250702.dto.ReturnRequest;
import homework.librarymanagementsys250702.entity.BorrowingRecord;
import homework.librarymanagementsys250702.service.BookService;
import homework.librarymanagementsys250702.service.BorrowingService;
import homework.librarymanagementsys250702.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户Controller
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 搜索书籍
     */
    @GetMapping("/books/search")
    public Result<List<BookDTO>> searchBooks(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) String author,
                                           @RequestParam(required = false) String category,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "12") int size) {
        List<BookDTO> books = bookService.searchBooks(keyword, title, author, category, page, size);
        return Result.success(books);
    }
    
    /**
     * 借阅书籍
     */
    @PostMapping("/borrow")
    public Result<String> borrowBook(@Valid @RequestBody BorrowRequest borrowRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        borrowingService.borrowBook(userId, borrowRequest);
        return Result.success("借阅成功");
    }
    
    /**
     * 归还书籍（简单版本，保持兼容性）
     */
    @PostMapping("/return/{recordId}")
    public Result<String> returnBook(@PathVariable Long recordId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        borrowingService.returnBook(userId, recordId);
        return Result.success("归还成功");
    }
    
    /**
     * 归还书籍（支持不同归还类型）
     */
    @PostMapping("/return-with-type/{recordId}")
    public Result<String> returnBookWithType(@PathVariable Long recordId, 
                                           @Valid @RequestBody ReturnRequest returnRequest,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        borrowingService.returnBookWithType(userId, recordId, returnRequest);
        return Result.success("归还成功");
    }
    
    /**
     * 获取我的借阅记录
     */
    @GetMapping("/borrowing-records")
    public Result<List<BorrowingRecordDTO>> getMyBorrowingRecords(HttpServletRequest request,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate) {
        Long userId = (Long) request.getAttribute("userId");
        List<BorrowingRecordDTO> records = borrowingService.getUserBorrowingRecordsWithBookInfo(userId, status, startDate, endDate);
        return Result.success(records);
    }

    /**
     * 获取客户统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("currentBorrows", borrowingService.getCurrentBorrowCount(userId));
        stats.put("totalBorrows", borrowingService.getTotalBorrowCount(userId));
        stats.put("overdueBorrows", borrowingService.getOverdueBorrowCount(userId));
        
        return Result.success(stats);
    }
    
    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> profile = userService.getUserProfile(userId);
        return Result.success(profile);
    }
    
    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Map<String, Object> profileData, 
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateUserProfile(userId, profileData);
        return Result.success("个人信息更新成功");
    }
    
    /**
     * 账户充值
     */
    @PostMapping("/recharge")
    public Result<String> recharge(@RequestBody Map<String, Object> requestData, 
                                   HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BigDecimal amount = new BigDecimal(requestData.get("amount").toString());
        userService.recharge(userId, amount);
        return Result.success("充值成功");
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestParam String oldPassword, 
                                         @RequestParam String newPassword, 
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
} 