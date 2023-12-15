package com.example.mapper;

import com.example.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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

}
