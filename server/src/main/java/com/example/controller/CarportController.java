package com.example.controller;


import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.CarportDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Carport;
import com.example.exception.AccountLockedException;
import com.example.mapper.CarportMapper;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.ICarportService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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

    @Resource
    private CarportMapper carportMapper;

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

    /**
     * 根据ID删除车场
     * @param id 车场ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        // 根据ID查询车场
        Carport carport = carportMapper.selectById(id);

        // 如果车场不存在，则抛出异常
        if (carport == null) {
            throw new AccountLockedException(MessageConstant.CARPORT_NOT_FOUND);
        }

        // 更新车场的更新时间和更新用户
        carport.setUpdateTime(LocalDateTime.now());
        carport.setUpdateUser(BaseContext.getCurrentId());

        // 更新车场信息
        carportMapper.updateById(carport);

        // 删除车场
        carportMapper.deleteById(id);

        // 返回删除成功的结果
        return Result.success();
    }


    /**
     * 批量删除
     * @param ids 要删除的ID列表
     * @return 删除结果
     */
    @DeleteMapping("/deletebatch")
    public Result deleteBatch(@RequestBody List<Long> ids){

        carportService.deleteBatch(ids);

        return Result.success();
    }

}

