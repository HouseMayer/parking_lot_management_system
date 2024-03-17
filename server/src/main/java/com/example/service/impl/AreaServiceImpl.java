package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.AreaDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Area;
import com.example.exception.AccountLockedException;
import com.example.exception.LoginException;
import com.example.mapper.AreaMapper;
import com.example.mapper.CarportMapper;
import com.example.result.PageResult;
import com.example.service.IAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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

    @Resource
    private AreaMapper areaMapper;

    @Resource
    private CarportMapper carportMapper;
    /**
     * 分页查询区域信息
     * @param pageQueryDTO 区域分页查询参数
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

    /**
     * 保存区域信息
     * @param areaDTO 区域信息DTO对象
     */
    @Override
    public void save(AreaDTO areaDTO) {
        Area area = new Area();


        // 对象属性拷贝
        BeanUtils.copyProperties(areaDTO, area);

        // 检查区域名是否已存在
        if (areaMapper.getByAreaName(area.getName()) != null ){
            throw new LoginException(MessageConstant.AREA_ALREADY_EXISTS);
        }

        area.setDeleted(0);
        areaMapper.insertArea(area);
    }

    /**
     * 更新区域信息
     * @param areaDTO 包含区域信息的DTO对象
     */
    @Override
    public void update(AreaDTO areaDTO) {
        Long id = areaDTO.getId();
        Area area = this.getById(id);
        // 检查区域名是否已存在
        if (!areaDTO.getName().equals(area.getName())){
            if (areaMapper.getByAreaName(areaDTO.getName()) != null ){
                throw new LoginException(MessageConstant.AREA_ALREADY_EXISTS);
            }
            area.setName(areaDTO.getName());
            areaMapper.updateById(area);
        }
    }

    /**
     * 批量删除区域
     * @param ids id列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 遍历id列表
        for (Long id : ids) {
            // 根据ID查询区域
            Area area = this.getById(id);
            // 如果区域不存在，则抛出区域不存在异常
            if (area == null) {
                throw new AccountLockedException(MessageConstant.AREA_NOT_FOUND);
            }

            // 检查区域是否关联有车位
            if(carportMapper.countByAreaId(Math.toIntExact(id)) > 0){
                throw new AccountLockedException(MessageConstant.AREA_EXISTS_CARPORT);
            }

            // 设置更新时间为当前时间
            area.setUpdateTime(LocalDateTime.now());
            // 设置更新用户为当前用户
            area.setUpdateUser(BaseContext.getCurrentId());

            // 更新区域对象
            areaMapper.updateById(area);
        }

        // 执行批量删除操作
        areaMapper.deleteBatchIds(ids);
    }


    /**
     * 根据ID删除区域
     * @param id 区域ID
     */
    @Override
    public void deleteById(Integer id) {
        // 根据ID查询区域
        Area area = areaMapper.selectById(id);

        // 如果区域不存在，则抛出区域不存在异常
        if (area == null) {
            throw new AccountLockedException(MessageConstant.AREA_NOT_FOUND);
        }

        // 检查区域是否关联有车位
        if(carportMapper.countByAreaId(id) > 0){
            throw new AccountLockedException(MessageConstant.AREA_EXISTS_CARPORT);
        }

        // 更新区域的更新时间和更新用户
        area.setUpdateTime(LocalDateTime.now());
        area.setUpdateUser(BaseContext.getCurrentId());

        // 更新区域信息
        areaMapper.updateById(area);

        // 删除区域
        areaMapper.deleteById(id);
    }

}
