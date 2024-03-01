package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.annotation.AutoFill;
import com.example.entity.Carport;
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
public interface CarportMapper extends BaseMapper<Carport> {

    @Select("select * from carport where area = #{areaId}")
    List<Carport> getByAreaId(Long areaId);

    @Select("select count(*) from carport where area = #{areaId}")
    Integer countByAreaId(Integer areaId);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into carport " +
            "(carport, area, type, classify, license_plate, state, deleted, update_time, update_user, create_time, create_user) " +
            "VALUES " +
            "(#{carport}, #{area}, #{type}, #{classify}, #{licensePlate}, #{state}, #{deleted}, #{updateTime}, #{updateUser}, #{createTime}, #{createUser})")
    void insertCarport(Carport carport);

    @Select("select * from carport where carport = #{carport}")
    Carport getByCarport(String carport);
}
