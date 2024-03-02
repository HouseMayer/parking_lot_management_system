package com.example.controller;


import com.example.dto.AccessRecordDTO;
import com.example.dto.RecordPageQueryDTO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAccess_recordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 *  记录
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@RestController
@RequestMapping("/record")
public class Access_recordController {

    @Resource
    private IAccess_recordService recordService;

    /**
     * 获取分页数据
     *
     * @param recordPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult> list(RecordPageQueryDTO recordPageQueryDTO) {

        // 调用服务层的分页查询方法
        PageResult pageResult = recordService.pageQuery(recordPageQueryDTO);

        // 返回成功结果
        return Result.success(pageResult);
    }

    /**
     * 保存访问记录
     * @param accessRecordDTO 访问记录DTO对象
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result save(@RequestBody AccessRecordDTO accessRecordDTO) {

        // 调用记录服务保存访问记录
        recordService.save(accessRecordDTO);

        // 返回保存成功的结果
        return Result.success();
    }

    /**
     * 更新访问记录
     * @param accessRecordDTO 访问记录DTO对象
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody AccessRecordDTO accessRecordDTO) {

        // 调用服务层更新访问记录
        recordService.update(accessRecordDTO);

        // 返回更新结果
        return Result.success();
    }

}

