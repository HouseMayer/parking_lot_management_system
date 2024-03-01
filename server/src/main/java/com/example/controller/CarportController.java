package com.example.controller;


import com.example.dto.CarportDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Carport;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.ICarportService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 *  车位
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@RestController
@RequestMapping("/carport")
public class CarportController {

    @Resource
    private ICarportService carportService;

    /**
     * 获取分页数据
     *
     * @param pageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult> list(PageQueryDTO pageQueryDTO) {

        // 调用服务层的分页查询方法
        PageResult pageResult = carportService.pageQuery(pageQueryDTO);

        // 返回成功结果
        return Result.success(pageResult);
    }



    /**
     * 根据id获取Carport对象
     * @param id Carport对象的id
     * @return 返回包含Carport对象的Result对象
     */
    @GetMapping("/{id}")
    public Result<Carport> getById(@PathVariable Integer id) {
        Carport carport = carportService.getById(id);
        return Result.success(carport);
    }


    /**
     * 保存车场信息
     * @param carportDTO 车场信息
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result save(@RequestBody CarportDTO carportDTO) {

        carportService.save(carportDTO);

        return Result.success();
    }

    /**
     * 更新车位信息
     * @param carportDTO 车位信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody CarportDTO carportDTO) {
    
        // 调用服务层更新车位信息
        carportService.update(carportDTO);
    
        // 返回更新结果
        return Result.success();
    }



}

