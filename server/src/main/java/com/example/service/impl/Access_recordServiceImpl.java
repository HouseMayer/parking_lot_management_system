package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.AccessRecordDTO;
import com.example.dto.RecordPageQueryDTO;
import com.example.entity.AccessRecord;
import com.example.mapper.Access_recordMapper;
import com.example.mapper.CarportMapper;
import com.example.result.PageResult;
import com.example.service.IAccess_recordService;
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
public class Access_recordServiceImpl extends ServiceImpl<Access_recordMapper, AccessRecord> implements IAccess_recordService {

    @Resource
    private Access_recordMapper recordMapper;
    @Resource
    private CarportMapper carportMapper;

    /**
     * 分页查询记录
     * @param recordPageQueryDTO 记录分页查询DTO对象
     * @return 分页查询结果对象
     */
    @Override
    public PageResult pageQuery(RecordPageQueryDTO recordPageQueryDTO) {

        // 如果参数为空，则抛出运行时异常
        if (recordPageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取当前页面、每页数量和名称
        int currentPage = recordPageQueryDTO.getPage();
        int pageSize = recordPageQueryDTO.getPageSize();

        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper qw = new QueryWrapper();
        qw.gt("start_time",recordPageQueryDTO.getStart());
        qw.lt("start_time",recordPageQueryDTO.getEnd());
        qw.like("license_plate", recordPageQueryDTO.getKeyword());

        // 创建分页对象
        IPage<AccessRecord> page = new Page<>(currentPage, pageSize);

        // 执行查询，并获取查询结果列表
        IPage<AccessRecord> pageRes = recordMapper.selectPage(page, qw);
        //将数据库内数字转换为意义内容
        pageRes.getRecords().forEach( en -> {
            if (en.getCarport()!= null){
                en.setCarport(carportMapper.getCarportById(en.getCarport()));
            }
        });
        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(pageRes.getRecords());
        log.info("分页查询结果"+pageRes.getRecords().toString());
        pageResult.setTotal(pageRes.getTotal());

        // 返回分页查询结果
        return pageResult;
    }

    @Override
    public void save(AccessRecordDTO accessRecordDTO) {
        AccessRecord accessRecord = new AccessRecord();

        BeanUtils.copyProperties(accessRecordDTO, accessRecord);

        accessRecord.setDeleted(0);
        recordMapper.insertRecord(accessRecord);
    }

}
