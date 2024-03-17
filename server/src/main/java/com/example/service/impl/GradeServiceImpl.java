package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.constant.StateConstant;
import com.example.context.BaseContext;
import com.example.dto.GradeDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Grade;
import com.example.exception.AccountLockedException;
import com.example.exception.LoginException;
import com.example.mapper.GradeMapper;
import com.example.result.PageResult;
import com.example.service.IGradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
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
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements IGradeService {

    @Resource
    private GradeMapper gradeMapper;

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
        String licensePlate = pageQueryDTO.getKeyword();
        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper<Grade> wrapper = new QueryWrapper<Grade>()
                .like("license_plate", licensePlate)
                .orderByDesc("deadline");

        // 创建分页对象
        IPage<Grade> page = new Page<>(currentPage, pageSize);

        // 执行查询，并获取查询结果列表
        IPage<Grade> pageRes = gradeMapper.selectPage(page, wrapper);

        // 遍历查询结果列表，根据等级进行处理
        pageRes.getRecords().forEach( en -> {
            if ("0".equals(en.getGrade())) {
                en.setGrade(StateConstant.BLACKLIST);
            } else if ("1".equals(en.getGrade())) {
                en.setGrade(StateConstant.VIP);
            }
        });

        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(pageRes.getRecords());
        log.info("分页查询结果" + pageRes.getRecords().toString());
        pageResult.setTotal(pageRes.getTotal());

        // 返回分页查询结果
        return pageResult;
    }

    /**
     * 保存GradeDTO对象
     * @param gradeDTO 要保存的对象
     */
    @Override
    public void save(GradeDTO gradeDTO) {
        Grade grade = new Grade();

        // 对象属性拷贝
        BeanUtils.copyProperties(gradeDTO, grade);

        grade.setDeadline(LocalDate.parse(gradeDTO.getDeadline()));

        // 检查车牌是否已存在
        if (gradeMapper.getLicensePlate(grade.getLicensePlate()) != null ){
            throw new LoginException(MessageConstant.LICENSE_PLATE_ALREADY_EXISTS);
        }

        grade.setDeleted(0);
        gradeMapper.insertGrade(grade);
    }

    /**
     * 批量删除车牌信息
     * @param ids 要删除的车牌ID列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 遍历id列表
        for (Long id : ids) {
            // 根据ID查询信息
            Grade grade = this.getById(id);
            // 如果信息不存在，则抛出车牌不存在异常
            if (grade == null) {
                throw new AccountLockedException(MessageConstant.LICENSE_PLATE_NOT_FOUND);
            }

            // 设置更新时间为当前时间
            grade.setUpdateTime(LocalDateTime.now());
            // 设置更新用户为当前用户
            grade.setUpdateUser(BaseContext.getCurrentId());

            // 更新对象
            gradeMapper.updateById(grade);
        }

        // 执行批量删除操作
        gradeMapper.deleteBatchIds(ids);
    }

    /**
     * 更新车牌信息
     * @param gradeDTO 车牌信息DTO
     */
    @Override
    public void update(GradeDTO gradeDTO) {
        Long id = gradeDTO.getId();
        Grade grade = this.getById(id);
        if (grade == null ) {
            throw new AccountLockedException(MessageConstant.LICENSE_PLATE_NOT_FOUND);
        }
        // 检查车牌是否已存在
        if (!gradeDTO.getLicensePlate().equals(grade.getLicensePlate())){
            if (gradeMapper.getByLicensePlate(gradeDTO.getLicensePlate()) != null ){
                throw new LoginException(MessageConstant.LICENSE_PLATE_ALREADY_EXISTS);
            }
        }

        BeanUtils.copyProperties(gradeDTO, grade);
        LocalDate deadline = LocalDate.parse(gradeDTO.getDeadline());

        grade.setDeadline(deadline);

        gradeMapper.updateById(grade);
    }

}
