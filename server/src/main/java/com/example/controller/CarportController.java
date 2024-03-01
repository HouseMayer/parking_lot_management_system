package com.example.controller;


import com.example.dto.PageQueryDTO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.ICarportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

