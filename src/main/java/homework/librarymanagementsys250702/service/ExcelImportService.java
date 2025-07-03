package homework.librarymanagementsys250702.service;

import homework.librarymanagementsys250702.dto.BookDTO;
import homework.librarymanagementsys250702.dto.UserDTO;
import homework.librarymanagementsys250702.entity.Book;
import homework.librarymanagementsys250702.entity.User;
import homework.librarymanagementsys250702.repository.BookRepository;
import homework.librarymanagementsys250702.repository.UserRepository;
import homework.librarymanagementsys250702.utils.PasswordUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件导入服务类 (支持Excel和CSV)
 */
@Service
public class ExcelImportService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    /**
     * 导入用户数据 (支持Excel和CSV)
     */
    public String importUsers(MultipartFile file) throws IOException {
        List<User> users = parseUsers(file);
        
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();
        
        for (User user : users) {
            try {
                // 检查用户名是否已存在
                User existing = userRepository.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                        .eq("username", user.getUsername())
                );
                
                if (existing != null) {
                    errors.add("用户名 " + user.getUsername() + " 已存在");
                    failureCount++;
                    continue;
                }
                
                // 设置默认密码
                user.setPassword(PasswordUtil.encode("123456"));
                user.setUserType(0); // 默认为客户
                user.setBalance(BigDecimal.ZERO);
                user.setStatus(0); // 正常状态
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                user.setDeleted(0);
                
                userRepository.insert(user);
                successCount++;
            } catch (Exception e) {
                errors.add("导入用户 " + user.getUsername() + " 失败: " + e.getMessage());
                failureCount++;
            }
        }
        
        StringBuilder result = new StringBuilder();
        result.append("导入完成！成功: ").append(successCount).append("条，失败: ").append(failureCount).append("条");
        
        if (!errors.isEmpty()) {
            result.append("\n错误信息:\n");
            for (String error : errors) {
                result.append("- ").append(error).append("\n");
            }
        }
        
        return result.toString();
    }
    
    /**
     * 导入书籍数据 (支持Excel和CSV)
     */
    public String importBooks(MultipartFile file) throws IOException {
        List<Book> books = parseBooks(file);
        
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();
        
        for (Book book : books) {
            try {
                // 检查ISBN是否已存在
                if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
                    Book existing = bookRepository.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Book>()
                            .eq("isbn", book.getIsbn())
                    );
                    
                    if (existing != null) {
                        errors.add("ISBN " + book.getIsbn() + " 已存在");
                        failureCount++;
                        continue;
                    }
                }
                
                // 设置默认值
                book.setAvailableQuantity(book.getStockQuantity());
                book.setStatus(0); // 正常状态
                book.setCreateTime(LocalDateTime.now());
                book.setUpdateTime(LocalDateTime.now());
                book.setDeleted(0);
                
                bookRepository.insert(book);
                successCount++;
            } catch (Exception e) {
                errors.add("导入书籍 " + book.getTitle() + " 失败: " + e.getMessage());
                failureCount++;
            }
        }
        
        StringBuilder result = new StringBuilder();
        result.append("导入完成！成功: ").append(successCount).append("条，失败: ").append(failureCount).append("条");
        
        if (!errors.isEmpty()) {
            result.append("\n错误信息:\n");
            for (String error : errors) {
                result.append("- ").append(error).append("\n");
            }
        }
        
        return result.toString();
    }
    
    /**
     * 解析用户数据 (自动识别Excel和CSV格式)
     */
    private List<User> parseUsers(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
            return parseUsersFromCSV(file);
        } else {
            return parseUsersFromExcel(file);
        }
    }
    
    /**
     * 解析书籍数据 (自动识别Excel和CSV格式)
     */
    private List<Book> parseBooks(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
            return parseBooksFromCSV(file);
        } else {
            return parseBooksFromExcel(file);
        }
    }
    
    /**
     * 从CSV解析用户数据
     */
    private List<User> parseUsersFromCSV(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();
        
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            for (CSVRecord record : csvParser) {
                User user = new User();
                
                // 用户名 (必填)
                String username = record.get("用户名").trim();
                if (username.isEmpty()) {
                    continue; // 跳过没有用户名的行
                }
                user.setUsername(username);
                
                // 真实姓名 (必填)
                String realName = record.get("真实姓名").trim();
                if (realName.isEmpty()) {
                    continue; // 跳过没有真实姓名的行
                }
                user.setRealName(realName);
                
                // 性别 (可选)
                String genderStr = record.get("性别").trim();
                if (!genderStr.isEmpty()) {
                    if ("男".equals(genderStr) || "1".equals(genderStr)) {
                        user.setGender(1);
                    } else if ("女".equals(genderStr) || "0".equals(genderStr)) {
                        user.setGender(0);
                    }
                }
                
                // 年龄 (可选)
                String ageStr = record.get("年龄").trim();
                if (!ageStr.isEmpty()) {
                    try {
                        user.setAge(Integer.parseInt(ageStr));
                    } catch (NumberFormatException e) {
                        // 忽略年龄解析错误
                    }
                }
                
                // 手机号 (可选)
                String phone = record.get("手机号").trim();
                if (!phone.isEmpty()) {
                    user.setPhone(phone);
                }
                
                // 邮箱 (可选)
                String email = record.get("邮箱").trim();
                if (!email.isEmpty()) {
                    user.setEmail(email);
                }
                
                users.add(user);
            }
        }
        
        return users;
    }
    
    /**
     * 从CSV解析书籍数据
     */
    private List<Book> parseBooksFromCSV(MultipartFile file) throws IOException {
        List<Book> books = new ArrayList<>();
        
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            for (CSVRecord record : csvParser) {
                Book book = new Book();
                
                // 书名 (必填)
                String title = record.get("书名").trim();
                if (title.isEmpty()) {
                    continue; // 跳过没有书名的行
                }
                book.setTitle(title);
                
                // 作者 (必填)
                String author = record.get("作者").trim();
                if (author.isEmpty()) {
                    continue; // 跳过没有作者的行
                }
                book.setAuthor(author);
                
                // ISBN (可选)
                String isbn = record.get("ISBN").trim();
                if (!isbn.isEmpty()) {
                    book.setIsbn(isbn);
                }
                
                // 分类 (必填)
                String category = record.get("分类").trim();
                if (category.isEmpty()) {
                    book.setCategory("其他"); // 默认分类
                } else {
                    book.setCategory(category);
                }
                
                // 出版社 (可选)
                String publisher = record.get("出版社").trim();
                if (!publisher.isEmpty()) {
                    book.setPublisher(publisher);
                }
                
                // 出版年份 (可选)
                String publishYearStr = record.get("出版年份").trim();
                if (!publishYearStr.isEmpty()) {
                    try {
                        book.setPublishYear(Integer.parseInt(publishYearStr));
                    } catch (NumberFormatException e) {
                        // 忽略年份解析错误
                    }
                }
                
                // 库存数量 (可选，默认为1)
                String stockStr = record.get("库存数量").trim();
                if (!stockStr.isEmpty()) {
                    try {
                        book.setStockQuantity(Integer.parseInt(stockStr));
                    } catch (NumberFormatException e) {
                        book.setStockQuantity(1); // 默认库存
                    }
                } else {
                    book.setStockQuantity(1); // 默认库存
                }
                
                // 价格 (可选，默认为0)
                String priceStr = record.get("价格").trim();
                if (!priceStr.isEmpty()) {
                    try {
                        book.setPrice(new BigDecimal(priceStr));
                    } catch (NumberFormatException e) {
                        book.setPrice(BigDecimal.ZERO);
                    }
                } else {
                    book.setPrice(BigDecimal.ZERO);
                }
                
                // 描述 (可选)
                String description = record.get("描述").trim();
                if (!description.isEmpty()) {
                    book.setDescription(description);
                }
                
                books.add(book);
            }
        }
        
        return books;
    }
    
    /**
     * 从Excel解析用户数据
     */
    private List<User> parseUsersFromExcel(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过标题行，从第2行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                User user = new User();
                
                // 用户名 (必填)
                Cell usernameCell = row.getCell(0);
                if (usernameCell == null || getCellValueAsString(usernameCell).trim().isEmpty()) {
                    continue; // 跳过没有用户名的行
                }
                user.setUsername(getCellValueAsString(usernameCell).trim());
                
                // 真实姓名 (必填)
                Cell realNameCell = row.getCell(1);
                if (realNameCell == null || getCellValueAsString(realNameCell).trim().isEmpty()) {
                    continue; // 跳过没有真实姓名的行
                }
                user.setRealName(getCellValueAsString(realNameCell).trim());
                
                // 性别 (可选)
                Cell genderCell = row.getCell(2);
                if (genderCell != null) {
                    String genderStr = getCellValueAsString(genderCell).trim();
                    if ("男".equals(genderStr) || "1".equals(genderStr)) {
                        user.setGender(1);
                    } else if ("女".equals(genderStr) || "0".equals(genderStr)) {
                        user.setGender(0);
                    }
                }
                
                // 年龄 (可选)
                Cell ageCell = row.getCell(3);
                if (ageCell != null) {
                    try {
                        user.setAge((int) ageCell.getNumericCellValue());
                    } catch (Exception e) {
                        // 忽略年龄解析错误
                    }
                }
                
                // 手机号 (可选)
                Cell phoneCell = row.getCell(4);
                if (phoneCell != null) {
                    user.setPhone(getCellValueAsString(phoneCell).trim());
                }
                
                // 邮箱 (可选)
                Cell emailCell = row.getCell(5);
                if (emailCell != null) {
                    user.setEmail(getCellValueAsString(emailCell).trim());
                }
                
                users.add(user);
            }
        }
        
        return users;
    }
    
    /**
     * 从Excel解析书籍数据
     */
    private List<Book> parseBooksFromExcel(MultipartFile file) throws IOException {
        List<Book> books = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过标题行，从第2行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Book book = new Book();
                
                // 书名 (必填)
                Cell titleCell = row.getCell(0);
                if (titleCell == null || getCellValueAsString(titleCell).trim().isEmpty()) {
                    continue; // 跳过没有书名的行
                }
                book.setTitle(getCellValueAsString(titleCell).trim());
                
                // 作者 (必填)
                Cell authorCell = row.getCell(1);
                if (authorCell == null || getCellValueAsString(authorCell).trim().isEmpty()) {
                    continue; // 跳过没有作者的行
                }
                book.setAuthor(getCellValueAsString(authorCell).trim());
                
                // ISBN (可选)
                Cell isbnCell = row.getCell(2);
                if (isbnCell != null) {
                    book.setIsbn(getCellValueAsString(isbnCell).trim());
                }
                
                // 分类 (必填)
                Cell categoryCell = row.getCell(3);
                if (categoryCell == null || getCellValueAsString(categoryCell).trim().isEmpty()) {
                    book.setCategory("其他"); // 默认分类
                } else {
                    book.setCategory(getCellValueAsString(categoryCell).trim());
                }
                
                // 出版社 (可选)
                Cell publisherCell = row.getCell(4);
                if (publisherCell != null) {
                    book.setPublisher(getCellValueAsString(publisherCell).trim());
                }
                
                // 出版年份 (可选)
                Cell publishYearCell = row.getCell(5);
                if (publishYearCell != null) {
                    try {
                        book.setPublishYear((int) publishYearCell.getNumericCellValue());
                    } catch (Exception e) {
                        // 尝试解析字符串
                        try {
                            book.setPublishYear(Integer.parseInt(getCellValueAsString(publishYearCell)));
                        } catch (Exception ex) {
                            // 忽略年份解析错误
                        }
                    }
                }
                
                // 库存数量 (可选，默认为1)
                Cell stockCell = row.getCell(6);
                if (stockCell != null) {
                    try {
                        book.setStockQuantity((int) stockCell.getNumericCellValue());
                    } catch (Exception e) {
                        book.setStockQuantity(1); // 默认库存
                    }
                } else {
                    book.setStockQuantity(1); // 默认库存
                }
                
                // 价格 (可选，默认为0)
                Cell priceCell = row.getCell(7);
                if (priceCell != null) {
                    try {
                        book.setPrice(BigDecimal.valueOf(priceCell.getNumericCellValue()));
                    } catch (Exception e) {
                        book.setPrice(BigDecimal.ZERO);
                    }
                } else {
                    book.setPrice(BigDecimal.ZERO);
                }
                
                // 描述 (可选)
                Cell descCell = row.getCell(8);
                if (descCell != null) {
                    book.setDescription(getCellValueAsString(descCell).trim());
                }
                
                books.add(book);
            }
        }
        
        return books;
    }
    
    /**
     * 获取单元格值作为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
} 