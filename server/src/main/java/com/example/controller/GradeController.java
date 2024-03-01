package com.example.controller;


import com.example.dto.PageQueryDTO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IGradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

}

