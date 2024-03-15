package com.example.controller;


import com.example.constant.JwtClaimsConstant;
import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.AdminDTO;
import com.example.dto.AdminLoginDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Admin;
import com.example.exception.AccountLockedException;
import com.example.mapper.AdminMapper;
import com.example.properties.JwtProperties;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAdminService;
import com.example.utils.JwtUtil;
import com.example.vo.AdminInfoVO;
import com.example.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 *  用户
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private IAdminService adminService;
    @Resource
    private AdminMapper adminMapper;
    @Resource
    private JwtProperties jwtProperties;


    /**
     * 根据用户分页查询条件获取页面数据
     *
     * @param pageQueryDTO 用户分页查询DTO对象
     * @return 结果对象，包含分页结果数据
     */
    @GetMapping("/page")
    public Result<PageResult> list(PageQueryDTO pageQueryDTO) {

        PageResult pageResult = adminService.pageQuery(pageQueryDTO);

        return Result.success(pageResult);
    }


    /**
     * 根据id获取信息
     *
     * @param id id
     * @return 返回信息
     */
    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Integer id) {
        Admin admin = adminService.getById(id);
        return Result.success(admin);
    }


    @GetMapping("/self")
    public Result<Admin> getByToken() {
        Integer id = Math.toIntExact(BaseContext.getCurrentId());
        Admin admin = adminService.getById(id);
        return Result.success(admin);
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
    public Result save(@RequestBody AdminDTO adminDTO) {

        adminService.save(adminDTO);

        return Result.success();
    }

    /**
     * 更新管理员信息
     * @param adminDTO 管理员信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody AdminDTO adminDTO) {

        // 调用服务层更新管理员信息
        adminService.update(adminDTO);

        // 返回更新结果
        return Result.success();
    }


    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Transactional
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


    /**
     * 批量删除数据
     * @param ids 要删除的数据id列表
     * @return 删除结果
     */
    @DeleteMapping("/deletebatch")
    public Result deleteBatch(@RequestBody List<Long> ids){

        adminService.deleteBatch(ids);

        return Result.success();
    }











}

