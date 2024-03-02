package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.AccessRecordDTO;
import com.example.dto.RecordPageQueryDTO;
import com.example.entity.AccessRecord;
import com.example.result.PageResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
public interface IAccess_recordService extends IService<AccessRecord> {

    PageResult pageQuery(RecordPageQueryDTO recordPageQueryDTO);

    void save(AccessRecordDTO accessRecordDTO);
}
