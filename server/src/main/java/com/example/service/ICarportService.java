package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.CarportDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Carport;
import com.example.result.PageResult;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
public interface ICarportService extends IService<Carport> {

    PageResult pageQuery(PageQueryDTO pageQueryDTO);

    void save(CarportDTO carportDTO);

    void update(CarportDTO carportDTO);
}
