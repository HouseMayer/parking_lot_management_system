package com.example.controller;


import com.example.dto.PageQueryDTO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IAccess_recordService;
import com.example.service.IUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Resource
    private IUploadService uploadService;
    @Resource
    private IAccess_recordService recordService;
    /**
     * 处理文件上传的请求。
     *
     * @param file 用户上传的文件，类型为MultipartFile。
     * @return 返回一个表示操作结果的对象，如果上传成功，则返回一个成功标志。
     * @throws IOException 如果在处理文件过程中发生IO异常。
     */
    @RequestMapping("/in")
    public Result uploadIn(MultipartFile file) throws IOException {
        // 将上传的文件交由uploadService处理
        uploadService.in(file);

        // 返回操作成功的结果
        return Result.success();
    }


    /**
     * 处理文件上传的请求。
     *
     * @param file 用户上传的文件，类型为MultipartFile。
     * @return 返回一个表示操作成功的Result对象。
     * @throws IOException 如果在处理文件过程中发生IO异常。
     */
    @RequestMapping("/out")
    public Result uploadOut(MultipartFile file) throws IOException {
        // 将文件转交给uploadService进行处理
        uploadService.out(file);

        // 返回操作成功的结果
        return Result.success();
    }


    @RequestMapping("/page")
    public Result<PageResult> list(PageQueryDTO pageQueryDTO) throws IOException {

        PageResult pageResult = recordService.pageQueryIn(pageQueryDTO);

        return Result.success(pageResult);
    }

}
