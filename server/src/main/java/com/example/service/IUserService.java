package com.example.service;

import com.example.dto.UserLoginDTO;
import com.example.dto.UserPageQueryDTO;
import com.example.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.result.PageResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
public interface IUserService extends IService<User> {

    PageResult pageQuery(UserPageQueryDTO userPageQueryDTO);

    User login(UserLoginDTO userLoginDTO);
}
