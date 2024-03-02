package com.example.controller;


import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.GradeDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Grade;
import com.example.exception.AccountLockedException;
import com.example.mapper.GradeMapper;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IGradeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 *  VIP等级
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@RestController
@RequestMapping("/grade")
public class GradeController {

    @Resource
    private IGradeService gradeService;

    @Resource
    private GradeMapper gradeMapper;

    /**
     * 分页查询
     * @param pageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public Result<PageResult> list(PageQueryDTO pageQueryDTO) {

        // 调用服务层的分页查询方法
        PageResult pageResult = gradeService.pageQuery(pageQueryDTO);

        // 返回查询结果
        return Result.success(pageResult);
    }

    /**
     * 根据id获取Grade对象
     * @param id Grade对象的id
     * @return 返回获取到的Grade对象
     */
    @GetMapping("/{id}")
    public Result<Grade> getById(@PathVariable Integer id) {
        Grade grade = gradeService.getById(id);
        return Result.success(grade);
    }

    /**
     * 保存VIP信息
     * @param gradeDTO VIP信息
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result save(@RequestBody GradeDTO gradeDTO) {

        gradeService.save(gradeDTO);

        return Result.success();
    }

    /**
     * 更新年级信息
     * @param gradeDTO 年级信息DTO
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody GradeDTO gradeDTO) {

        // 调用服务层更新信息
        gradeService.update(gradeDTO);

        // 返回更新结果
        return Result.success();
    }

    /**
     * 根据ID删除记录
     * @param id ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Transactional
    public Result deleteById(@PathVariable Integer id) {
        // 根据ID查询记录
        Grade grade = gradeMapper.selectById(id);

        // 如果记录为空，则抛出异常
        if (grade == null) {
            throw new AccountLockedException(MessageConstant.LICENSE_PLATE_NOT_FOUND);
        }

        // 更新记录的更新时间和更新用户
        grade.setUpdateTime(LocalDateTime.now());
        grade.setUpdateUser(BaseContext.getCurrentId());

        // 更新记录
        gradeMapper.updateById(grade);

        // 删除记录
        gradeMapper.deleteById(id);

        // 返回删除成功的结果
        return Result.success();
    }

    /**
     * 批量删除数据
     * @param ids 要删除的数据id列表
     * @return 删除结果
     */
    @DeleteMapping("/deletebatch")
    public Result deleteBatch(@RequestBody List<Long> ids){

        // 调用服务层的批量删除方法
        gradeService.deleteBatch(ids);

        // 返回删除成功的结果
        return Result.success();
    }

}

