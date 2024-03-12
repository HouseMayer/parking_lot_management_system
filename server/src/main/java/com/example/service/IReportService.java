package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.ExportDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Report;
import com.example.result.PageResult;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
public interface IReportService extends IService<Report> {

    void export(ExportDTO exportDTO);

    void batchExport(List<Long> ids);

    PageResult pageQuery(PageQueryDTO pageQueryDTO);
}
