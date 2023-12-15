package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.UserLoginDTO;
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

    /**
     * 分页查询方法
     *
     * @param userPageQueryDTO 用户分页查询DTO对象
     * @return 分页查询结果PageResult对象
     */
    @Override
    public PageResult pageQuery(UserPageQueryDTO userPageQueryDTO) {

        // 如果参数为空，则抛出运行时异常
        if (userPageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取当前页面、每页数量和名称
        int currentPage = userPageQueryDTO.getPage();
        int pageSize = userPageQueryDTO.getPageSize();
        String name = userPageQueryDTO.getName();

        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper<User> wrapper = new QueryWrapper<User>().like("name", name);

        // 创建分页对象
        IPage<User> page = new Page<>(currentPage, pageSize);

        // 执行查询，并获取查询结果列表
        List<User> userList = list(page, wrapper);

        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(userList);
        pageResult.setTotal(userList.size());

        // 返回分页查询结果
        return pageResult;
    }



}
