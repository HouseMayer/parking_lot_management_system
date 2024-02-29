package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.AreaDTO;
import com.example.dto.AreaPageQueryDTO;
import com.example.entity.Area;
import com.example.entity.Carport;
import com.example.entity.User;
import com.example.exception.AccountLockedException;
import com.example.exception.LoginException;
import com.example.mapper.AreaMapper;
import com.example.mapper.CarportMapper;
import com.example.result.PageResult;
import com.example.service.IAreaService;
import com.example.service.ICarportService;
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
    private ICarportService carportService;

    @Resource
    private CarportMapper carportMapper;
    /**
     * 分页查询区域信息
     * @param areaPageQueryDTO 区域分页查询参数
     * @return 分页查询结果
     */
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

    /**
     * 保存区域信息
     * @param areaDTO 区域信息DTO对象
     */
    @Override
    public void save(AreaDTO areaDTO) {
        Area area = new Area();
        User user = new User();

        // 对象属性拷贝
        BeanUtils.copyProperties(areaDTO, area);

        // 检查区域名是否已存在
        if (areaMapper.getByAreaName(area.getName()) != null ){
            throw new LoginException(MessageConstant.AREA_ALREADY_EXISTS);
        }

        user.setDeleted(0);
        areaMapper.insertArea(user);
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
            if (areaMapper.getByAreaName(area.getName()) != null ){
                throw new LoginException(MessageConstant.AREA_ALREADY_EXISTS);
            }
            area.setName(areaDTO.getName());
            List<Carport> carportList = carportMapper.getByAreaId(Math.toIntExact(id));
            carportList.forEach( en -> {
                en.setArea(areaDTO.getName());
                carportMapper.updateById(en);
            });
            areaMapper.updateById(area);
        }
    }

    /**
     * 批量删除管理员
     * @param ids 管理员id列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 遍历管理员id列表
        for (Long id : ids) {
            // 检查管理员id对应的区域是否存在
            if(carportService.getByAreaId(Math.toIntExact(id)) != null){
                throw new AccountLockedException(MessageConstant.AREA_EXISTS_CARPORT);
            }

            // 根据管理员id获取管理员对象
            Area area = this.getById(id);
            System.out.println(area);

            // 设置管理员更新时间为当前时间
            area.setUpdateTime(LocalDateTime.now());
            // 设置管理员更新用户为当前用户
            area.setUpdateUser(BaseContext.getCurrentId());

            // 更新管理员对象
            areaMapper.updateById(area);
        }

        // 执行批量删除操作
        areaMapper.deleteBatchIds(ids);
    }

}
