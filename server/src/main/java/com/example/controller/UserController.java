package com.example.controller;


import com.example.constant.JwtClaimsConstant;
import com.example.dto.UserDTO;
import com.example.dto.UserLoginDTO;
import com.example.dto.UserPageQueryDTO;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.properties.JwtProperties;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IUserService;
import com.example.utils.JwtUtil;
import com.example.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 根据用户分页查询条件获取页面数据
     *
     * @param userPageQueryDTO 用户分页查询DTO对象
     * @return 结果对象，包含分页结果数据
     */
    @GetMapping("/page")
    public Result<PageResult> list(UserPageQueryDTO userPageQueryDTO) {

        PageResult pageResult = userService.pageQuery(userPageQueryDTO);


        return Result.success(pageResult);
    }

    /**
     * 员工登录
     *
     * @param userLoginDTO 员工登录信息
     * @return 用户登录结果
     */
    @PostMapping("/login")
    public Result<UserLoginVO> list(UserLoginDTO userLoginDTO) {
        log.info("员工登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .name(user.getName())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }










    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {

        userMapper.deleteById(id);

        return Result.success();
    }










}

