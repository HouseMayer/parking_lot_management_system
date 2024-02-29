package com.example.controller;


import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.AreaDTO;
import com.example.dto.AreaPageQueryDTO;
import com.example.entity.Area;
import com.example.exception.AccountLockedException;
import com.example.mapper.AreaMapper;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 *  区域
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Slf4j
@RestController
@RequestMapping("/area")
public class AreaController {

    @Autowired
    private IAreaService areaService;

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 分页查询区域列表
     * @param areaPageQueryDTO 区域查询参数
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public Result<PageResult> list(AreaPageQueryDTO areaPageQueryDTO) {

        // 调用服务层的分页查询方法
        PageResult pageResult = areaService.pageQuery(areaPageQueryDTO);

        // 返回查询结果
        return Result.success(pageResult);
    }

    /**
     * 根据id获取区域信息
     * @param id 区域id
     * @return 区域信息
     */
    @GetMapping("/{id}")
    public Result<Area> getById(@PathVariable Integer id) {
        // 调用服务层获取指定id的区域信息
        Area area = areaService.getById(id);
        // 返回成功的结果，包含获取到的区域信息
        return Result.success(area);
    }


    @PostMapping("/save")
    public Result save(@RequestBody AreaDTO areaDTO) {

        areaService.save(areaDTO);

        return Result.success();
    }


    /**
     * 更新区域信息
     * @param areaDTO 区域信息DTO对象
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody AreaDTO areaDTO) {

        // 调用服务层更新管理员信息
        areaService.update(areaDTO);

        // 返回更新结果
        return Result.success();
    }


    /**
     * 根据ID删除区域
     * @param id 区域ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        // 根据ID查询区域
        Area area = areaMapper.selectById(id);

        // 如果区域不存在，则抛出区域不存在异常
        if (area == null) {
            throw new AccountLockedException(MessageConstant.AREA_NOT_FOUND);
        }

        // 更新区域的更新时间和更新用户
        area.setUpdateTime(LocalDateTime.now());
        area.setUpdateUser(BaseContext.getCurrentId());

        // 更新区域信息
        areaMapper.updateById(area);

        // 删除区域
        areaMapper.deleteById(id);

        // 返回删除成功的结果
        return Result.success();
    }


    /**
     * 批量删除
     * @param ids 要删除的id列表
     * @return 删除结果
     */
    @DeleteMapping("/deletebatch")
    public Result deleteBatch(@RequestBody List<Long> ids){

        // 调用服务层的批量删除方法
        areaService.deleteBatch(ids);

        // 返回删除成功的结果
        return Result.success();
    }


}

