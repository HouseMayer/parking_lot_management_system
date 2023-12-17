package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.annotation.AutoFill;
import com.example.entity.User;
import com.example.enumeration.OperationType;
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
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where user_name = #{username}")
    User getByUserName(String username);

    @AutoFill(OperationType.INSERT)
    @Select("insert into user" +
            "(name,user_name,phone,password,role,deleted,update_time,update_user,create_time,create_user) " +
            "values" +
            "(#{name},#{userName},#{phone},#{password},#{role},#{deleted},#{updateTime},#{updateUser},#{createTime},#{createUser})")
    void insertUser(User user);
}
