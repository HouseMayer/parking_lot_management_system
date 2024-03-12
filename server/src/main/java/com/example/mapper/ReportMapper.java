package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.annotation.AutoFill;
import com.example.dto.ReportTaskDTO;
import com.example.entity.Report;
import com.example.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {

    @Results({
            @Result(column = "count(*)", property = "total"),
            @Result(column = "sum(cost)", property = "earning")
    })
    @Select("select count(*), sum(cost) from access_record where end_time between #{begin} and #{end}")
    ReportTaskDTO getCountCostByDate(String begin, String end);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into report(record_date, total, earning, deleted, update_time, update_user, create_time, create_user)" +
            " values" +
            "(#{recordDate}, #{total}, #{earning}, #{deleted}, #{updateTime}, #{updateUser}, #{createTime}, #{createUser})")
    void insertReport(Report report);
}
