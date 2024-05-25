package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.dto.ExportDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.AccessRecord;
import com.example.entity.Report;
import com.example.exception.FileException;
import com.example.mapper.Access_recordMapper;
import com.example.mapper.ReportMapper;
import com.example.result.PageResult;
import com.example.service.IReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements IReportService {

    @Resource
    private Access_recordMapper recordMapper;
    @Resource
    private ReportMapper reportMapper;

    /**
     * 导出报表功能实现。
     * 该方法根据给定的开始时间和结束时间，查询这段时间内的访问记录，并将这些记录以及统计信息导出到一个Excel文件中。
     *
     * @param exportDTO 包含导出所需数据的DTO对象，其中包括开始时间、结束时间和HTTP响应对象。
     *                  开始时间和结束时间用于查询访问记录，HTTP响应对象用于向客户端发送导出的Excel文件。
     */
    @Override
    public void export(ExportDTO exportDTO) {

        // 从DTO中获取开始时间、结束时间和响应对象
        String start = exportDTO.getStart();
        String end = exportDTO.getEnd();

        // 将字符串形式的开始时间和结束时间转换为LocalDateTime对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        LocalDateTime startTime = LocalDateTime.parse(start, formatter).plusHours(8);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter).plusHours(8);

        // 构建查询条件
        QueryWrapper qw = new QueryWrapper();
        qw.ge("end_time", startTime);
        qw.le("end_time", endTime);

        // 根据查询条件查询访问记录，并计算总条数和总费用
        List<AccessRecord> selectList = recordMapper.selectList(qw);
        start = startTime.toLocalDate().toString();
        end = endTime.toLocalDate().toString();
        String name;
        if (start.equals(end)){
            name = start + "报表";
        }else{
            name = start + "~" + end +"报表";
        }

        editReportTemplate(selectList, start, end, name);

    }

    /**
     * 批量导出功能的实现。
     * 该方法接收一个包含需要导出的数据的ID和响应对象的DTO，用于收集导出数据的相关信息和处理导出后的响应。
     * @param ids 需要导出的数据的ID列表。
     */
    @Override
    public void batchExport(List<Long> ids) {
        // 根据构建的查询条件查询访问记录列表
        List<AccessRecord> recordList = recordMapper.selectBatchIds(ids);

        // 通过流操作计算访问记录列表中的最早和最晚结束时间
        Optional<LocalDateTime> min = recordList.stream().map(AccessRecord::getEndTime).min(LocalDateTime::compareTo);
        Optional<LocalDateTime> max = recordList.stream().map(AccessRecord::getEndTime).max(LocalDateTime::compareTo);

        // 提取Optional中的实际开始和结束时间，并转换为字符串格式
        String start = min.get().toString();
        String end = max.get().toString();
        String name = "临时报表";
        // 使用访问记录列表、开始和结束时间编辑报告模板，并通过响应对象将报告返回给客户端
        editReportTemplate(recordList, start, end, name);

    }

    @Override
    public void exportById(String reportDate) {
        LocalDate date = LocalDate.parse(reportDate);
        String begin = date.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = date.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime beginTime = LocalDateTime.parse(begin, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 构建查询条件
        QueryWrapper qw = new QueryWrapper();
        qw.ge("end_time", beginTime);
        qw.le("end_time", endTime);

        // 根据查询条件查询访问记录，并计算总条数和总费用
        List<AccessRecord> selectList = recordMapper.selectList(qw);
        String name = date + "报表";

        editReportTemplate(selectList, begin, end, name);
    }

    /**
     * 分页查询功能
     *
     * @param pageQueryDTO 包含分页参数和查询关键字的数据传输对象，不能为空
     * @return PageResult 分页查询结果对象，包含当前页的记录列表和总记录数
     * @throws RuntimeException 如果传入的pageQueryDTO为null，则抛出运行时异常
     */
    @Override
    public PageResult pageQuery(PageQueryDTO pageQueryDTO) {

        // 校验参数是否为空
        if (pageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }
        // 解析分页参数和查询关键字
        int currentPage = pageQueryDTO.getPage();
        int pageSize = pageQueryDTO.getPageSize();
        String keyword = pageQueryDTO.getKeyword();

        // 创建查询条件，实现模糊查询
        QueryWrapper<Report> wrapper;
        if (keyword == null || keyword.equals("")) {

            wrapper = new QueryWrapper<>();

        } else {
            wrapper = new QueryWrapper<Report>().eq("record_date", keyword);

        }
        wrapper.orderByDesc("record_date");
        log.info("wrapper:{}", wrapper.getSqlSelect());

        // 初始化分页对象
        IPage<Report> page = new Page<>(currentPage, pageSize);

        // 执行分页查询
        IPage<Report> pageRes = reportMapper.selectPage(page, wrapper);

        // 组装分页查询结果
        PageResult pageResult = new PageResult();
        pageResult.setRecords(pageRes.getRecords()); // 设置当前页记录列表
        log.info("分页查询结果: " + pageRes.getRecords().toString());
        pageResult.setTotal(pageRes.getTotal()); // 设置总记录数

        // 返回分页查询结果
        return pageResult;
    }



    /**
     * 编辑并导出访问记录报告的模板
     *
     * @param selectList 选择的访问记录列表
     * @param start 开始时间（字符串格式）
     * @param end 结束时间（字符串格式）
     * @param name 报表名
     */
    public void editReportTemplate(List<AccessRecord> selectList, String start, String end, String name){

        float costTotal = 0;

        int total = selectList.size();
        // 计算所有访问记录的总花费
        for (AccessRecord accessRecord : selectList) {
            costTotal += accessRecord.getCost().floatValue();
        }
        // 加载Excel模板文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/ReportTemplates.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            // 填充Excel表格的汇总数据
            XSSFRow row = sheet1.getRow(3);
            row.getCell(2).setCellValue(start);
            row.getCell(4).setCellValue(total);
            row.getCell(6).setCellValue(costTotal);

            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(end);
            row.getCell(4).setCellValue(total == 0 ? 0 : costTotal/total);

            // 填充访问记录详情
            for (int i = 0; i < selectList.size(); i++) {
                AccessRecord accessRecord = selectList.get(i);
                row = sheet1.getRow(7 + i);
                row.getCell(1).setCellValue(accessRecord.getEndTime().toLocalDate().toString());
                row.getCell(2).setCellValue(accessRecord.getLicensePlate());
                if (accessRecord.getStartTime() == null){
                    row.getCell(3).setCellValue("");
                } else {
                    row.getCell(3).setCellValue(accessRecord.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                if (accessRecord.getEndTime() == null){
                    row.getCell(3).setCellValue("");
                } else {
                    row.getCell(4).setCellValue(accessRecord.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                if (accessRecord.getStartTime() == null || accessRecord.getEndTime() == null){
                    row.getCell(5).setCellValue(0);
                } else {
                    row.getCell(5).setCellValue(Duration.between(accessRecord.getStartTime(), accessRecord.getEndTime()).toMinutes()/60);
                }
                row.getCell(6).setCellValue(accessRecord.getCost().floatValue());
            }
            File file = new File("C:/Users/lb267/Desktop/报表/" + name + ".xlsx");
            if (file.exists() && file.isDirectory()) {
                throw new FileException(MessageConstant.INVALID_DOCUMENT);
            }
            if (file.exists() && !file.canWrite()) {
                log.info("qwewqewe");
                throw new FileException(MessageConstant.FILE_BE_USED);
            }

            FileOutputStream out = new FileOutputStream("C:/Users/lb267/Desktop/报表/" + name + ".xlsx");
            excel.write(out);
            out.close();
            excel.close();
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
