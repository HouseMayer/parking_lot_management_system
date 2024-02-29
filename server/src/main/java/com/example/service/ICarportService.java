package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Carport;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
public interface ICarportService extends IService<Carport> {

    List<Carport> getByAreaId(Integer id);
}
