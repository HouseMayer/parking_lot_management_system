package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.AreaDTO;
import com.example.dto.AreaPageQueryDTO;
import com.example.entity.Area;
import com.example.result.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
public interface IAreaService extends IService<Area> {

    PageResult pageQuery(AreaPageQueryDTO areaPageQueryDTO);

    void save(AreaDTO areaDTO);

    @Transactional
    void update(AreaDTO areaDTO);

    @Transactional
    void deleteBatch(List<Long> ids);
}
