package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.GradeDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Grade;
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
public interface IGradeService extends IService<Grade> {

    PageResult pageQuery(PageQueryDTO pageQueryDTO);

    void save(GradeDTO gradeDTO);

    @Transactional
    void deleteBatch(List<Long> ids);
}
