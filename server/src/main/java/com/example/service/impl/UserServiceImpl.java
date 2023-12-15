package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.UserPageQueryDTO;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.result.PageResult;
import com.example.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public PageResult pageQuery(UserPageQueryDTO userPageQueryDTO) {

        if (userPageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }

        int currentPage = userPageQueryDTO.getPage();
        int pageSize = userPageQueryDTO.getPageSize();
        String name = userPageQueryDTO.getName();

        QueryWrapper<User> wrapper = new QueryWrapper<User>().like("name", name);

        IPage<User> page = new Page<>(currentPage, pageSize);

        List<User> userList = list(page, wrapper);

        PageResult pageResult = new PageResult();
        pageResult.setRecords(userList);
        pageResult.setTotal(userList.size());

        return pageResult;
    }
}
