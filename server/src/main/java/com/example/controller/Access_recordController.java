package com.example.controller;


import com.example.dto.RecordPageQueryDTO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAccess_recordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private IAccess_recordService arService;

    /**
     * 获取分页数据
     *
     * @param recordPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult> list(RecordPageQueryDTO recordPageQueryDTO) {

        // 调用服务层的分页查询方法
        PageResult pageResult = arService.pageQuery(recordPageQueryDTO);

        // 返回成功结果
        return Result.success(pageResult);
    }


}

