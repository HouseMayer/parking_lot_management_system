package com.example.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.context.BaseContext;
import com.example.dto.ReportTaskDTO;
import com.example.entity.Grade;
import com.example.entity.Report;
import com.example.mapper.GradeMapper;
import com.example.mapper.ReportMapper;
import com.example.service.IGradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class reportTask {

    @Resource
    private ReportMapper reportMapper;
    @Resource
    private GradeMapper gradeMapper;
    @Resource
    private IGradeService gradeService;
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void reportTask() {
        log.info("开始reportTask");


        VIP();

        LocalDate yesterday = LocalDate.now().minusDays(1);
        String begin = yesterday.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = yesterday.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ReportTaskDTO countCostByDate = reportMapper.getCountCostByDate(begin, end);
        Report report = new Report();
        if (countCostByDate != null){
            report.setTotal(countCostByDate.getTotal());
            report.setEarning(countCostByDate.getEarning() == null ?
                    BigDecimal.valueOf(0) : countCostByDate.getEarning());
        } else {
            report.setTotal(0);
            report.setEarning(BigDecimal.valueOf(0));
        }

        report.setRecordDate(yesterday);
        report.setDeleted(0);
        reportMapper.insertReport(report);
    }
//    @Scheduled(cron = "0 0/1 * * * ?") // 每分钟执行一次
    public void reportTaskTest() {
        log.info("开始reportTaskTest");
        LocalDate day = LocalDate.of(2024, 2, 15);

        while (day.isBefore(LocalDate.of(2024, 3, 23))){
            String begin = day.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String end = day.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            ReportTaskDTO countCostByDate = reportMapper.getCountCostByDate(begin, end);
            Report report = new Report();
            if (countCostByDate != null){

                report.setTotal(countCostByDate.getTotal());
                report.setEarning(countCostByDate.getEarning() == null ? BigDecimal.valueOf(0) : countCostByDate.getEarning());
            } else {
                report.setTotal(0);
                report.setEarning(BigDecimal.valueOf(0));
            }

            report.setRecordDate(day);
            report.setDeleted(0);

            reportMapper.insertReport(report);

            day = day.plusDays(1);
        }
    }


    @Transactional
    public void VIP() {

        QueryWrapper qw = new QueryWrapper<>();
        qw.le("deadline", LocalDate.now());
        qw.select("id");
        List<Integer> ids = gradeMapper.selectList(qw);
        for (Integer id : ids) {
            Grade grade = gradeService.getById(id);
            grade.setUpdateTime(LocalDateTime.now());
            grade.setUpdateUser(BaseContext.getCurrentId());
            // 更新车辆泊位信息
            gradeMapper.updateById(grade);
        }

        gradeMapper.deleteBatchIds(ids);
    }
}
