package com.example.service.impl;

import com.example.entity.Report;
import com.example.mapper.ReportMapper;
import com.example.service.IReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements IReportService {

}
