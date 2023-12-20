package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-17
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select role from role where id = #{roleId}")
    String getById(Integer roleId);

}
