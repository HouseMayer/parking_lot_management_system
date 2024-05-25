package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.annotation.AutoFill;
import com.example.entity.Area;
import com.example.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Mapper
public interface AreaMapper extends BaseMapper<Area> {

    @Select("select * from area where name = #{name}")
    Area getByAreaName(String name);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into area(name,deleted,update_time,update_user,create_time,create_user) " +
            "values" +
            "(#{name},#{deleted},#{updateTime},#{updateUser},#{createTime},#{createUser})")
    void insertArea(Area area);

    @Select("select name from area where id = #{area}")
    String getNameById(String area);
}
