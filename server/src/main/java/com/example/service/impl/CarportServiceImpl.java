package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.dto.CarportDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Carport;
import com.example.exception.LoginException;
import com.example.mapper.CarportMapper;
import com.example.result.PageResult;
import com.example.service.ICarportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
public class CarportServiceImpl extends ServiceImpl<CarportMapper, Carport> implements ICarportService {

    @Resource
    private CarportMapper carportMapper;


    /**
     * 分页查询
     * @param pageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    @Override
    public PageResult pageQuery(PageQueryDTO pageQueryDTO) {
        // 如果参数为空，则抛出运行时异常
        if (pageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取当前页面、每页数量和名称
        int currentPage = pageQueryDTO.getPage();
        int pageSize = pageQueryDTO.getPageSize();
        String name = pageQueryDTO.getKeyword();

        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper<Carport> wrapper = new QueryWrapper<Carport>().like("name", name);

        // 创建分页对象
        IPage<Carport> page = new Page<>(currentPage, pageSize);

        // 执行查询，并获取查询结果列表
        IPage<Carport> pageRes = carportMapper.selectPage(page, wrapper);

        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(pageRes.getRecords());
        log.info("分页查询结果"+pageRes.getRecords().toString());
        pageResult.setTotal(pageRes.getTotal());

        // 返回分页查询结果
        return pageResult;
    }

    /**
     * 保存车位信息
     * @param carportDTO 车位信息DTO对象
     */
    @Override
    public void save(CarportDTO carportDTO) {
        Carport carport = new Carport();

        // 对象属性拷贝
        BeanUtils.copyProperties(carportDTO, carport);

        // 检查车位名是否已存在
        if (carportMapper.getByCarport(carport.getCarport()) != null ){
            throw new LoginException(MessageConstant.USERNAME_ALREADY_EXISTS);
        }

        carport.setDeleted(0);
        carportMapper.insertCarport(carport);

    }
}
