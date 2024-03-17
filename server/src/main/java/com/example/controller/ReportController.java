package com.example.controller;


import com.example.dto.ExportDTO;
import com.example.dto.PageQueryDTO;
import com.example.entity.Report;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 *  报表
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    @Resource
    private IReportService reportService;

    /**
     * 导出报告的接口。
     *
     * @param exportDTO 包含导出报告所需参数的数据传输对象，例如导出的格式、条件等。
     */
    @GetMapping("/export")
    public Result export(ExportDTO exportDTO) {

        // 调用报告服务，执行导出操作。此步骤将根据exportDTO中的参数执行具体的导出逻辑。
        reportService.export(exportDTO);

        // 返回操作成功的结果。此操作表明导出流程已正确执行，无需额外处理。
        return Result.success();
    }


    /**
     * 批量导出功能的处理方法。该方法接收一个报告ID列表，通过调用报告服务来实现报告的批量导出。
     *
     * @param ids 需要导出的报告的ID列表，以逗号分隔的字符串形式提供。这些ID用于指定具体要导出哪些报告。
     */
    @GetMapping("/batchexport")
    public Result batchExport(String ids) {

        // 将传入的以逗号分隔的ID字符串转换为长整型列表，以备报告服务的批量导出操作使用
        List<Long> idList = new ArrayList<>();
        String[] strings = ids.split(",");
        for (String s : strings) {
            idList.add(Long.valueOf(s));
        }

        // 调用报告服务的批量导出方法，传入需要导出的报告ID列表，实现报告的批量导出
        reportService.batchExport(idList);

        return Result.success();

    }

    /**
     * 分页查询接口
     *
     * @param pageQueryDTO 分页查询参数对象，包含查询条件及分页信息
     * @return 返回查询结果的包装对象，其中包含分页结果信息
     */
    @GetMapping("/page")
    public Result<PageResult> list(PageQueryDTO pageQueryDTO) {

        // 调用报告服务进行分页查询
        PageResult pageResult = reportService.pageQuery(pageQueryDTO);

        // 将查询结果包装成成功结果并返回
        return Result.success(pageResult);
    }


    /**
     * 根据报告ID导出报告。
     *
     * @param reportDate 日期，从URL路径变量中获取。
     * @return 返回一个包含指定报告的结果对象。如果查询成功，结果对象的状态为成功（success），并包含报告对象。
     */
    @GetMapping("/{reportDate}")
    public Result<Report> exportById(@PathVariable String reportDate) {
        reportService.exportById(reportDate);
        return Result.success();
    }



}

