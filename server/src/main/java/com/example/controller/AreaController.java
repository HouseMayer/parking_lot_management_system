package com.example.controller;


import com.example.dto.AdminPageQueryDTO;
import com.example.dto.AreaPageQueryDTO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/page")
    public Result<PageResult> list(AreaPageQueryDTO areaPageQueryDTO) {

        PageResult pageResult = areaService.pageQuery(areaPageQueryDTO);

        return Result.success(pageResult);
    }

}

