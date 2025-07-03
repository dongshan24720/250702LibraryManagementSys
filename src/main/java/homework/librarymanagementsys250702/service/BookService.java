package homework.librarymanagementsys250702.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import homework.librarymanagementsys250702.dto.BookDTO;
import homework.librarymanagementsys250702.entity.Book;
import homework.librarymanagementsys250702.exception.BusinessException;
import homework.librarymanagementsys250702.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 书籍Service
 */
@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    /**
     * 添加书籍
     */
    public void addBook(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        book.setAvailableQuantity(book.getStockQuantity());
        book.setStatus(0);
        
        bookRepository.insert(book);
    }
    
    /**
     * 更新书籍
     */
    public void updateBook(BookDTO bookDTO) {
        Book existingBook = bookRepository.selectById(bookDTO.getBookId());
        if (existingBook == null) {
            throw new BusinessException("书籍不存在");
        }
        
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        bookRepository.updateById(book);
    }
    
    /**
     * 删除书籍
     */
    public void deleteBook(Long bookId) {
        Book book = bookRepository.selectById(bookId);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        
        bookRepository.deleteById(bookId);
    }
    
    /**
     * 分页查询书籍
     */
    public IPage<BookDTO> getBookList(int current, int size, String keyword) {
        Page<Book> page = new Page<>(current, size);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("title", keyword)
                    .or().like("author", keyword)
                    .or().like("category", keyword)
                    .or().like("isbn", keyword);
        }
        
        IPage<Book> bookPage = bookRepository.selectPage(page, queryWrapper);
        
        Page<BookDTO> resultPage = new Page<>(current, size);
        resultPage.setTotal(bookPage.getTotal());
        resultPage.setRecords(bookPage.getRecords().stream()
                .map(this::convertToDTO)
                .toList());
        
        return resultPage;
    }
    
    /**
     * 搜索书籍
     */
    public List<BookDTO> searchBooks(String keyword) {
        List<Book> books = bookRepository.searchBooks(keyword);
        return books.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    /**
     * 高级搜索书籍（支持多条件和分页）
     */
    public List<BookDTO> searchBooks(String keyword, String title, String author, String category, int page, int size) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("title", keyword)
                .or().like("author", keyword)
                .or().like("category", keyword)
                .or().like("isbn", keyword)
            );
        }
        
        if (StringUtils.hasText(title)) {
            queryWrapper.like("title", title);
        }
        
        if (StringUtils.hasText(author)) {
            queryWrapper.like("author", author);
        }
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        queryWrapper.eq("status", 0); // 只显示可用的书籍
        
        List<Book> books = bookRepository.selectList(queryWrapper);
        return books.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    /**
     * 获取书籍统计
     */
    public List<Object> getBookStatistics() {
        return bookRepository.countByCategory();
    }
    
    /**
     * 转换为DTO
     */
    private BookDTO convertToDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        BeanUtils.copyProperties(book, bookDTO);
        return bookDTO;
    }
} 