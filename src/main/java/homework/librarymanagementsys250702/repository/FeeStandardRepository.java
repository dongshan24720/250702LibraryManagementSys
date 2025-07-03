package homework.librarymanagementsys250702.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import homework.librarymanagementsys250702.entity.FeeStandard;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收费标准Repository
 */
@Mapper
public interface FeeStandardRepository extends BaseMapper<FeeStandard> {
} 