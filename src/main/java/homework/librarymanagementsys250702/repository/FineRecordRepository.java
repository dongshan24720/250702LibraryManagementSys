package homework.librarymanagementsys250702.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import homework.librarymanagementsys250702.entity.FineRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 罚款记录Repository
 */
@Mapper
public interface FineRecordRepository extends BaseMapper<FineRecord> {
} 