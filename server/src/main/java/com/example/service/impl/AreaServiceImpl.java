package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.AdminPageQueryDTO;
import com.example.dto.AreaPageQueryDTO;
import com.example.entity.Admin;
import com.example.entity.Area;
import com.example.mapper.AreaMapper;
import com.example.result.PageResult;
import com.example.service.IAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Slf4j
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

    @Autowired
    private AreaMapper areaMapper;
    @Override
    public PageResult pageQuery(AreaPageQueryDTO areaPageQueryDTO) {

        // 如果参数为空，则抛出运行时异常
        if (areaPageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取当前页面、每页数量和名称
        int currentPage = areaPageQueryDTO.getPage();
        int pageSize = areaPageQueryDTO.getPageSize();
        String name = areaPageQueryDTO.getKeyword();
        log.info("当前页面："+currentPage);
        log.info("每页数量："+pageSize);
        log.info("名称："+name);

        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper<Area> wrapper = new QueryWrapper<Area>().like("name", name);

        // 创建分页对象
        IPage<Area> page = new Page<>(currentPage, pageSize);

        // 执行查询，并获取查询结果列表
        IPage<Area> pageRes = areaMapper.selectPage(page, wrapper);


        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(pageRes.getRecords());
        log.info("分页查询结果"+pageRes.getRecords().toString());
        pageResult.setTotal(pageRes.getTotal());

        // 返回分页查询结果
        return pageResult;

    }
}
