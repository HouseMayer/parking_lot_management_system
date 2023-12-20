package com.example.controller;


import com.example.constant.JwtClaimsConstant;
import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.AdminDTO;
import com.example.dto.AdminLoginDTO;
import com.example.dto.AdminPageQueryDTO;
import com.example.entity.Admin;
import com.example.exception.AccountLockedException;
import com.example.mapper.AdminMapper;
import com.example.mapper.RoleMapper;
import com.example.properties.JwtProperties;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAdminService;
import com.example.utils.JwtUtil;
import com.example.vo.AdminInfoVO;
import com.example.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private IAdminService adminService;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RoleMapper roleMapper;


    /**
     * 根据用户分页查询条件获取页面数据
     *
     * @param adminPageQueryDTO 用户分页查询DTO对象
     * @return 结果对象，包含分页结果数据
     */
    @GetMapping("/page")
    public Result<PageResult> list(AdminPageQueryDTO adminPageQueryDTO) {

        PageResult pageResult = adminService.pageQuery(adminPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 员工登录
     *
     * @param adminLoginDTO 员工登录信息
     * @return 用户登录结果
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("员工登录：{}", adminLoginDTO);

        Admin admin = adminService.login(adminLoginDTO);

        String role = roleMapper.getById(admin.getRole());

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, admin.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        return Result.success(new AdminLoginVO(token));
    }

    /**
     * 获取用户信息
     * @param token 用户的令牌
     * @return 用户信息结果
     */
    @GetMapping("/info")
    public Result<AdminInfoVO> info(String token) {

        AdminInfoVO adminInfoVO = adminService.info(token);

        // 返回用户信息结果
        return Result.success(adminInfoVO);
    }



    
    /**
     * 添加员工
     * @param adminDTO 用户信息
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result save(AdminDTO adminDTO) {

        adminService.save(adminDTO);

        return Result.success();
    }


    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        Admin admin = adminMapper.selectById(id);

        if (admin == null) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        admin.setUpdateTime(LocalDateTime.now());
        admin.setUpdateUser(BaseContext.getCurrentId());
        adminMapper.updateById(admin);
        adminMapper.deleteById(id);

        return Result.success();
    }


    @DeleteMapping
    public Result deleteBatch(@RequestParam List<Long> ids){



        adminService.deleteBatch(ids);


        return Result.success();
    }











}

