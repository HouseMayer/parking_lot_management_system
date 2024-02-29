package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.JwtClaimsConstant;
import com.example.constant.MessageConstant;
import com.example.constant.PasswordConstant;
import com.example.context.BaseContext;
import com.example.dto.AdminDTO;
import com.example.dto.AdminLoginDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.AccountNotFoundException;
import com.example.exception.LoginException;
import com.example.exception.PasswordErrorException;
import com.example.mapper.AdminMapper;
import com.example.mapper.RoleMapper;
import com.example.properties.JwtProperties;
import com.example.result.PageResult;
import com.example.service.IAdminService;
import com.example.utils.JwtUtil;
import com.example.vo.AdminInfoVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RoleMapper roleMapper;


    /**
     * 分页查询方法
     *
     * @param pageQueryDTO 用户分页查询DTO对象
     * @return 分页查询结果PageResult对象
     */
    @Override
    public PageResult pageQuery(PageQueryDTO pageQueryDTO) {

        // 如果参数为空，则抛出运行时异常
        if (pageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }
        // 获取当前页面、每页数量和名称
        int currentPage = pageQueryDTO.getPage();
        int pageSize = pageQueryDTO.getPageSize();
        String name = pageQueryDTO.getKeyword();
        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper<Admin> wrapper = new QueryWrapper<Admin>().like("name", name);
        // 创建分页对象
        IPage<Admin> page = new Page<>(currentPage, pageSize);
        // 执行查询，并获取查询结果列表
        IPage<Admin> pageRes = adminMapper.selectPage(page, wrapper);
        for (Admin record : pageRes.getRecords()) {
            record.setRole(roleMapper.getById(record.getRole()));
        }

        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(pageRes.getRecords());
        log.info("分页查询结果"+pageRes.getRecords().toString());
        pageResult.setTotal(pageRes.getTotal());

        // 返回分页查询结果
        return pageResult;
    }

    /**
     * 登陆
     *
     * @param adminLoginDTO 登陆DTO对象
     * @return user对象
     */
    @Override
    public Admin login(AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Admin admin = adminMapper.getByUserName(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (admin == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //进行md5加密，然后再进行比对

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(admin.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }


        //3、返回实体对象
        return admin;
    }

    /**
     * 保存管理员信息
     * @param adminDTO 管理员信息
     */
    @Override
    public void save(AdminDTO adminDTO) {
        User user = new User();

        // 对象属性拷贝
        BeanUtils.copyProperties(adminDTO, user);

        // 检查用户名是否已存在
        if (adminMapper.getByUserName(user.getUserName()) != null ){
            throw new LoginException(MessageConstant.USERNAME_ALREADY_EXISTS);
        }

        // 设置默认密码
        user.setPassword(DigestUtils.md5DigestAsHex(
                PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        user.setDeleted(0);
        adminMapper.insertUser(user);
    }

    /**
     * 更新管理员信息
     * @param adminDTO 管理员DTO对象
     */
    @Override
    public void update(AdminDTO adminDTO) {
        Long id = adminDTO.getId();
        Admin admin = adminMapper.getById(id);
        // 判断用户名是否已经存在
        if (!adminDTO.getUserName().equals(admin.getUserName())){
            if (adminMapper.getByUserName(adminDTO.getUserName()) != null) {
                throw new LoginException(MessageConstant.USERNAME_ALREADY_EXISTS);
            }
        }
        // 更新管理员信息
        admin.setName(adminDTO.getName());
        admin.setUserName(adminDTO.getUserName());
        admin.setRole(String.valueOf(adminDTO.getRole()));
        admin.setPhone(adminDTO.getPhone());
        admin.setUpdateTime(LocalDateTime.now());
        admin.setUpdateUser(BaseContext.getCurrentId());
        adminMapper.updateById(admin);
    }


    /**
     * 批量删除管理员
     * @param ids 管理员id列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {

        // 遍历管理员id列表
        for (Long id : ids) {
            // 根据管理员id获取管理员对象
            Admin admin = adminMapper.getById(id);
            // 设置管理员更新时间为当前时间
            admin.setUpdateTime(LocalDateTime.now());
            // 设置管理员更新用户为当前用户
            admin.setUpdateUser(BaseContext.getCurrentId());

            // 更新管理员对象
            adminMapper.updateById(admin);
        }

        // 执行批量删除操作
        adminMapper.deleteBatchIds(ids);

    }

    /**
     * 根据令牌获取管理员信息
     *
     * @param token 令牌
     * @return 管理员信息
     */
    @Override
    public AdminInfoVO info(String token) {

        // 解析令牌获取claims
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);

        // 获取员工id
        Long id = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());

        // 根据id获取用户信息
        Admin admin = adminMapper.getById(id);

        // 获取用户的角色
        String role = roleMapper.getById(admin.getRole());
        ArrayList<String> roles = new ArrayList<>();
        roles.add(role);

        // 构建并返回AdminInfoVO对象
        return AdminInfoVO.builder()
                .avatar(admin.getAvatar())
                .introduction(admin.getIntroduction())
                .name(admin.getName())
                .roles(roles)
                .build();
    }



}
