package homework.librarymanagementsys250702.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import homework.librarymanagementsys250702.entity.BorrowingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 借阅记录Repository
 */
@Mapper
public interface BorrowingRecordRepository extends BaseMapper<BorrowingRecord> {
    
    /**
     * 查询用户正在借阅的书籍
     */
    @Select("SELECT * FROM borrowing_records WHERE user_id = #{userId} AND status = 0 AND deleted = 0")
    List<BorrowingRecord> findBorrowingByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户所有借阅记录
     */
    @Select("SELECT * FROM borrowing_records WHERE user_id = #{userId} AND deleted = 0 ORDER BY borrow_time DESC")
    List<BorrowingRecord> findAllBorrowingByUserId(@Param("userId") Long userId);
    
    /**
     * 查询超期记录
     */
    @Select("SELECT * FROM borrowing_records WHERE due_time < NOW() AND status = 0 AND deleted = 0")
    List<BorrowingRecord> findOverdueRecords();
    
    /**
     * 统计借阅情况
     */
    @Select("SELECT b.category, COUNT(*) as count FROM borrowing_records br " +
            "JOIN books b ON br.book_id = b.book_id " +
            "WHERE br.deleted = 0 GROUP BY b.category")
    List<Object> countBorrowingByCategory();

    /**
     * 统计用户当前借阅数量
     */
    @Select("SELECT COUNT(*) FROM borrowing_records WHERE user_id = #{userId} AND status = #{status} AND deleted = #{deleted}")
    long countByUserIdAndStatusAndDeleted(Long userId, Integer status, Integer deleted);

    /**
     * 统计用户总借阅数量
     */
    @Select("SELECT COUNT(*) FROM borrowing_records WHERE user_id = #{userId} AND deleted = #{deleted}")
    long countByUserIdAndDeleted(Long userId, Integer deleted);

    /**
     * 统计用户逾期借阅数量
     */
    @Select("SELECT COUNT(*) FROM borrowing_records WHERE user_id = #{userId} AND status = #{status} AND due_time < #{date} AND deleted = #{deleted}")
    long countByUserIdAndStatusAndDueDateBeforeAndDeleted(Long userId, Integer status, Date date, Integer deleted);
    
    /**
     * 统计指定状态的借阅记录数量
     */
    @Select("SELECT COUNT(*) FROM borrowing_records WHERE status = #{status} AND deleted = #{deleted}")
    long countByStatusAndDeleted(Integer status, Integer deleted);
} 