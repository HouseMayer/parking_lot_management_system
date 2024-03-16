package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.annotation.AutoFill;
import com.example.entity.AccessRecord;
import com.example.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Mapper
public interface Access_recordMapper extends BaseMapper<AccessRecord> {

    @AutoFill(OperationType.INSERT)
    @Insert("insert into access_record " +
            "(license_plate, start_time, end_time, carport, cost, deleted, update_time, update_user, create_time, create_user) " +
            "VALUES " +
            "(#{licensePlate}, #{startTime}, #{endTime}, #{carport}, #{cost}, #{deleted}, #{updateTime}, #{updateUser}, #{createTime}, #{createUser})")
    void insertRecord(AccessRecord accessRecord);

    @Select("select * from access_record where license_plate = #{licensePlate} and end_time is null")
    List<AccessRecord> getByLicensePlate(String licensePlate);
}
