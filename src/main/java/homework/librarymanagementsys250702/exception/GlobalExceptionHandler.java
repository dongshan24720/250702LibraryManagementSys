package homework.librarymanagementsys250702.exception;

import homework.librarymanagementsys250702.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleValidationException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：{}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        return Result.error(400, message);
    }
    
    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException e) {
        log.error("参数绑定异常：{}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("参数绑定失败");
        return Result.error(400, message);
    }
    
    /**
     * 约束违反异常处理
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("约束违反异常：{}", e.getMessage());
        return Result.error(400, "参数校验失败");
    }
    
    /**
     * 数据库唯一约束违反异常处理
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Object> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据库唯一约束违反：{}", e.getMessage());
        String message = e.getMessage();
        
        // 根据错误信息判断具体的约束违反类型
        if (message.contains("isbn")) {
            return Result.error(400, "ISBN号码已存在，请检查后重新输入");
        } else if (message.contains("username")) {
            return Result.error(400, "用户名已存在，请使用其他用户名");
        } else if (message.contains("phone")) {
            return Result.error(400, "手机号码已被注册，请使用其他手机号");
        } else if (message.contains("email")) {
            return Result.error(400, "邮箱已被注册，请使用其他邮箱");
        } else {
            return Result.error(400, "数据已存在，请检查输入信息");
        }
    }
    
    /**
     * 其他异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error("系统异常，请联系管理员");
    }
} 