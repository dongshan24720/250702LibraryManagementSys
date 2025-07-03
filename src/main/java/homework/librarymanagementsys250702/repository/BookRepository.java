package homework.librarymanagementsys250702.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import homework.librarymanagementsys250702.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 书籍Repository
 */
@Mapper
public interface BookRepository extends BaseMapper<Book> {
    
    /**
     * 按分类统计书籍数量
     */
    @Select("SELECT category, COUNT(*) as count FROM books WHERE deleted = 0 GROUP BY category")
    List<Object> countByCategory();
    
    /**
     * 模糊查询书籍
     */
    @Select("SELECT * FROM books WHERE (title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR author LIKE CONCAT('%', #{keyword}, '%') " +
            "OR category LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND deleted = 0")
    List<Book> searchBooks(@Param("keyword") String keyword);
} 