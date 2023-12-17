package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.MessageConstant;
import com.example.constant.PasswordConstant;
import com.example.constant.StatusConstant;
import com.example.context.BaseContext;
import com.example.dto.UserDTO;
import com.example.dto.UserLoginDTO;
import com.example.dto.UserPageQueryDTO;
import com.example.entity.User;
import com.example.exception.AccountNotFoundException;
import com.example.exception.LoginException;
import com.example.exception.PasswordErrorException;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.result.PageResult;
import com.example.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
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
    @Autowired
    private UserMapper userMapper;


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

    /**
     * 登陆
     *
     * @param userLoginDTO 登陆DTO对象
     * @return user对象
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUserName();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUserName(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //进行md5加密，然后再进行比对

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }


        //3、返回实体对象
        return user;
    }

    @Override
    public void save(UserDTO userDTO) {
        User user = new User();

        // 对象属性拷贝
        BeanUtils.copyProperties(userDTO, user);

        if (userMapper.getByUserName(user.getUserName()) != null ){
            throw new LoginException(MessageConstant.USERNAME_ALREADY_EXISTS);
        }

        // 设置默认密码
        user.setPassword(DigestUtils.md5DigestAsHex(
                PasswordConstant.DEFAULT_PASSWORD.getBytes()));


        userMapper.insertUser(user);
    }

    @Override
    public void deleteBatch(List<Long> ids) {

        for (Long id : ids) {
            User user = getById(id);
            user.setDeleted(StatusConstant.DISENABLE);
            user.setUpdateTime(LocalDateTime.now());
            user.setUpdateUser(BaseContext.getCurrentId());
            updateById(user);
        }

    }

}
