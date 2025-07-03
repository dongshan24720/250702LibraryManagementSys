package homework.librarymanagementsys250702.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import homework.librarymanagementsys250702.dto.BookDTO;
import homework.librarymanagementsys250702.dto.BorrowingRecordDTO;
import homework.librarymanagementsys250702.dto.Result;
import homework.librarymanagementsys250702.dto.UserDTO;
import homework.librarymanagementsys250702.entity.BorrowingRecord;
import homework.librarymanagementsys250702.entity.FeeStandard;
import homework.librarymanagementsys250702.service.BookService;
import homework.librarymanagementsys250702.service.BorrowingService;
import homework.librarymanagementsys250702.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Controller
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    /**
     * 用户管理 - 查询用户列表
     */
    @GetMapping("/users")
    public Result<IPage<UserDTO>> getUserList(@RequestParam(defaultValue = "1") int current,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String search) {
        IPage<UserDTO> users = userService.getUserList(current, size, search);
        return Result.success(users);
    }
    
    /**
     * 用户管理 - 添加用户
     */
    @PostMapping("/users")
    public Result<String> addUser(@Valid @RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return Result.success("用户添加成功");
    }
    
    /**
     * 用户管理 - 更新用户
     */
    @PutMapping("/users/{userId}")
    public Result<String> updateUser(@PathVariable Long userId, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setUserId(userId);
        userService.updateUserByAdmin(userDTO);
        return Result.success("用户更新成功");
    }
    
    /**
     * 用户管理 - 删除用户
     */
    @DeleteMapping("/users/{userId}")
    public Result<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUserByAdmin(userId);
        return Result.success("用户删除成功");
    }
    
    /**
     * 用户管理 - 给用户充值
     */
    @PostMapping("/users/{userId}/recharge")
    public Result<String> rechargeUser(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        userService.recharge(userId, amount);
        return Result.success("充值成功");
    }
    
    /**
     * 用户管理 - 更新用户余额
     */
    @PutMapping("/users/{userId}/balance")
    public Result<String> updateUserBalance(@PathVariable Long userId, @RequestParam BigDecimal balance) {
        userService.updateUserBalance(userId, balance);
        return Result.success("余额更新成功");
    }
    
    /**
     * 书籍管理 - 查询书籍列表
     */
    @GetMapping("/books")
    public Result<IPage<BookDTO>> getBookList(@RequestParam(defaultValue = "1") int current,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String search) {
        IPage<BookDTO> books = bookService.getBookList(current, size, search);
        return Result.success(books);
    }
    
    /**
     * 书籍管理 - 添加书籍
     */
    @PostMapping("/books")
    public Result<String> addBook(@Valid @RequestBody BookDTO bookDTO) {
        bookService.addBook(bookDTO);
        return Result.success("书籍添加成功");
    }
    
    /**
     * 书籍管理 - 更新书籍
     */
    @PutMapping("/books/{bookId}")
    public Result<String> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookDTO bookDTO) {
        bookDTO.setBookId(bookId);
        bookService.updateBook(bookDTO);
        return Result.success("书籍更新成功");
    }
    
    /**
     * 书籍管理 - 删除书籍
     */
    @DeleteMapping("/books/{bookId}")
    public Result<String> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return Result.success("书籍删除成功");
    }
    
    /**
     * 借阅记录管理 - 查询所有借阅记录
     */
    @GetMapping("/borrow-records")
    public Result<IPage<BorrowingRecordDTO>> getBorrowRecords(@RequestParam(defaultValue = "1") int current,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(required = false) String username,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String startDate) {
        IPage<BorrowingRecordDTO> records = borrowingService.getBorrowingRecordsForAdmin(current, size, username, status, startDate);
        return Result.success(records);
    }
    
    /**
     * 借阅管理 - 查询超期记录
     */
    @GetMapping("/borrowing/overdue")
    public Result<List<BorrowingRecord>> getOverdueRecords() {
        List<BorrowingRecord> records = borrowingService.getOverdueRecords();
        return Result.success(records);
    }
    
    /**
     * 统计分析 - 书籍分类统计
     */
    @GetMapping("/statistics/books")
    public Result<List<Object>> getBookStatistics() {
        List<Object> statistics = bookService.getBookStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 统计分析 - 借阅情况统计
     */
    @GetMapping("/statistics/borrowing")
    public Result<List<Object>> getBorrowingStatistics() {
        List<Object> statistics = borrowingService.getBorrowingStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 获取管理员仪表板统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 统计总用户数
            IPage<UserDTO> userPage = userService.getUserList(1, 1, null);
            stats.put("totalUsers", userPage.getTotal());
            
            // 统计总书籍数
            IPage<BookDTO> bookPage = bookService.getBookList(1, 1, null);
            stats.put("totalBooks", bookPage.getTotal());
            
            // 统计当前借阅数（所有用户的当前借阅）
            // 这里我们需要统计status=0的借阅记录
            long activeBorrows = borrowingService.getActiveBorrowsCount();
            stats.put("activeBorrows", activeBorrows);
            
            // 统计逾期记录数
            List<BorrowingRecord> overdueRecords = borrowingService.getOverdueRecords();
            stats.put("overdueBooks", overdueRecords.size());
            
        } catch (Exception e) {
            // 如果统计失败，返回默认值
            stats.put("totalUsers", 0);
            stats.put("totalBooks", 0);
            stats.put("activeBorrows", 0);
            stats.put("overdueBooks", 0);
        }
        
        return Result.success(stats);
    }

    /**
     * 初始化系统数据
     */
    @PostMapping("/init-data")
    public Result<String> initData() {
        try {
            // 初始化书籍数据
            List<BookDTO> books = new ArrayList<>();
            
            // 文学类
            books.add(createBook("红楼梦", "曹雪芹", "9787020002207", "文学", "人民文学出版社", "2020-01-01", 10, new BigDecimal("2.00"), "中国古典文学四大名著之一"));
            books.add(createBook("三体", "刘慈欣", "9787536692930", "科幻", "重庆出版社", "2019-05-01", 15, new BigDecimal("3.00"), "科幻小说经典作品"));
            books.add(createBook("活着", "余华", "9787506365437", "文学", "作家出版社", "2018-08-01", 12, new BigDecimal("2.50"), "当代文学经典"));
            books.add(createBook("百年孤独", "加西亚·马尔克斯", "9787544253994", "文学", "南海出版公司", "2017-06-01", 8, new BigDecimal("3.50"), "魔幻现实主义代表作"));
            
            // 技术类
            books.add(createBook("Java核心技术", "Cay S. Horstmann", "9787111613978", "计算机", "机械工业出版社", "2021-03-01", 20, new BigDecimal("4.00"), "Java编程权威指南"));
            books.add(createBook("算法导论", "Thomas H. Cormen", "9787111407928", "计算机", "机械工业出版社", "2020-07-01", 15, new BigDecimal("5.00"), "算法学习经典教材"));
            books.add(createBook("深入理解计算机系统", "Randal E. Bryant", "9787111544937", "计算机", "机械工业出版社", "2019-11-01", 18, new BigDecimal("4.50"), "计算机系统权威指南"));
            books.add(createBook("Python编程：从入门到实践", "Eric Matthes", "9787115428028", "计算机", "人民邮电出版社", "2020-12-01", 25, new BigDecimal("3.00"), "Python学习入门书籍"));
            
            // 历史类
            books.add(createBook("史记", "司马迁", "9787101003048", "历史", "中华书局", "2018-01-01", 8, new BigDecimal("2.00"), "中国第一部纪传体通史"));
            books.add(createBook("明朝那些事儿", "当年明月", "9787505734227", "历史", "中国友谊出版公司", "2019-03-01", 14, new BigDecimal("2.50"), "明朝历史通俗读物"));
            
            // 科学类
            books.add(createBook("时间简史", "史蒂芬·霍金", "9787535764959", "科学", "湖南科技出版社", "2017-04-01", 12, new BigDecimal("3.00"), "宇宙学科普经典"));
            books.add(createBook("人类简史", "尤瓦尔·赫拉利", "9787508647357", "历史", "中信出版社", "2018-09-01", 16, new BigDecimal("3.50"), "人类发展史权威著作"));
            
            // 经济管理类
            books.add(createBook("经济学原理", "N.格里高利·曼昆", "9787301109540", "经济", "北京大学出版社", "2020-02-01", 10, new BigDecimal("4.00"), "经济学入门教材"));
            books.add(createBook("管理学", "罗宾斯", "9787300143729", "管理", "中国人民大学出版社", "2019-08-01", 12, new BigDecimal("3.50"), "管理学经典教材"));
            
            // 艺术类
            books.add(createBook("艺术的故事", "贡布里希", "9787544710800", "艺术", "译林出版社", "2018-05-01", 6, new BigDecimal("3.00"), "艺术史权威读物"));
            books.add(createBook("音乐之声", "茱莉·安德鲁斯", "9787506392847", "艺术", "作家出版社", "2017-12-01", 8, new BigDecimal("2.50"), "音乐艺术启蒙"));
            
            // 添加所有书籍
            for (BookDTO book : books) {
                try {
                    bookService.addBook(book);
                } catch (Exception e) {
                    // 忽略重复添加的错误
                    System.out.println("书籍可能已存在: " + book.getTitle());
                }
            }
            
            return Result.success("系统数据初始化成功，已添加 " + books.size() + " 本图书");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("初始化失败: " + e.getMessage());
        }
    }
    
    private BookDTO createBook(String title, String author, String isbn, String category, 
                              String publisher, String publishDate, Integer stockQuantity,
                              BigDecimal price, String description) {
        BookDTO book = new BookDTO();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setPublishYear(Integer.parseInt(publishDate.split("-")[0])); // 提取年份
        book.setStockQuantity(stockQuantity);
        book.setAvailableQuantity(stockQuantity);
        book.setPrice(price); // 使用price字段
        book.setDescription(description);
        book.setStatus(0);
        return book;
    }

} 