package com.example.task;


import com.example.dto.ReportTaskDTO;
import com.example.entity.Report;
import com.example.mapper.ReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class reportTask {

    @Resource
    private ReportMapper reportMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void reportTask() {
        log.info("开始reportTask");
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
        reportMapper.insertReport(report);
    }
//    @Scheduled(cron = "0 0/1 * * * ?")
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
            reportMapper.insertReport(report);

            day = day.plusDays(1);
        }
    }
}
