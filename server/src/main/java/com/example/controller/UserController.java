package com.example.controller;


import com.example.dto.UserPageQueryDTO;
import com.example.mapper.UserMapper;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;


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

