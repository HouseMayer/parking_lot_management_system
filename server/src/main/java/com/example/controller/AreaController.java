package com.example.controller;


import com.example.dto.AreaDTO;
import com.example.dto.AreaPageQueryDTO;
import com.example.entity.Area;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 *  区域
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@RestController
@RequestMapping("/area")
public class AreaController {

    @Autowired
    private IAreaService areaService;

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


}

