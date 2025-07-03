package homework.librarymanagementsys250702.controller;

import homework.librarymanagementsys250702.dto.Result;
import homework.librarymanagementsys250702.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件导入控制器 (支持Excel和CSV)
 */
@RestController
@RequestMapping("/admin/import")
public class ExcelImportController {
    
    @Autowired
    private ExcelImportService excelImportService;
    
    /**
     * 批量导入用户 (支持Excel和CSV)
     */
    @PostMapping("/users")
    public Result<String> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要导入的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                return Result.error("文件名不能为空");
            }
            
            String lowercaseFileName = fileName.toLowerCase();
            if (!lowercaseFileName.endsWith(".xlsx") && !lowercaseFileName.endsWith(".csv")) {
                return Result.error("请上传Excel(.xlsx)或CSV(.csv)格式文件");
            }
            
            String result = excelImportService.importUsers(file);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量导入书籍 (支持Excel和CSV)
     */
    @PostMapping("/books")
    public Result<String> importBooks(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要导入的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                return Result.error("文件名不能为空");
            }
            
            String lowercaseFileName = fileName.toLowerCase();
            if (!lowercaseFileName.endsWith(".xlsx") && !lowercaseFileName.endsWith(".csv")) {
                return Result.error("请上传Excel(.xlsx)或CSV(.csv)格式文件");
            }
            
            String result = excelImportService.importBooks(file);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入失败: " + e.getMessage());
        }
    }
} 