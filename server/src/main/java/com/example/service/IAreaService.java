package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.AreaDTO;
import com.example.dto.PageQueryDTO;
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

    PageResult pageQuery(PageQueryDTO pageQueryDTO);

    void save(AreaDTO areaDTO);

    void update(AreaDTO areaDTO);

    @Transactional
    void deleteBatch(List<Long> ids);

    void deleteById(Integer id);
}
