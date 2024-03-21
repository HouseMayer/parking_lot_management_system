package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.api.auth.AuthService;
import com.example.constant.MessageConstant;
import com.example.dto.AccessRecordDTO;
import com.example.exception.FileException;
import com.example.mapper.GradeMapper;
import com.example.properties.FilePathProperties;
import com.example.service.IAccess_recordService;
import com.example.service.IUploadService;
import com.example.utils.Base64Util;
import com.example.utils.FileUtil;
import com.example.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;


@Slf4j
@Service
public class UploadServiceImpl implements IUploadService {


    @Resource
    private AuthService authService;
    @Resource
    private IAccess_recordService access_recordService;
    @Resource
    private GradeMapper gradeMapper;
    @Resource
    private FilePathProperties filePathProperties;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    /**
     * 处理文件上传，将上传的文件保存到指定路径，并记录车牌号及访问记录。
     *
     * @param file 上传的文件对象，预期为车牌照片。
     * @throws IOException 当读写文件发生错误时抛出。
     */
    @Override
    public String in(MultipartFile file) throws IOException {
        // 获取文件保存路径
        String filePath = filePathProperties.getPath();
        // 从文件中提取车牌号
        String licensePlate = licensePlate(file);
        String originalFilename = file.getOriginalFilename();
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("license_plate", licensePlate);
        qw.eq("grade", "0");
        if (gradeMapper.selectList(qw).size() != 0) {
            // 构造原始文件名对应的文件对象，用于后续删除
            File fileToDelete = new File(filePath + "\\" + originalFilename);
            if (fileToDelete.exists()) {
                // 如果原始文件存在，则删除它
                fileToDelete.delete();
            } else {
                // 如果不存在，抛出文件未找到异常
                throw new FileException(MessageConstant.FILE_NOT_FOUND);
            }

            // 如果该车牌号已存在，则抛出该车牌号在黑名单异常
            throw new FileException(MessageConstant.LICENSE_PLATE_IN_BLACKLIST);
        }
        log.info("车牌号：" + licensePlate);
        // 构造新文件名，以车牌号命名
        File newFile = new File(filePath, licensePlate + ".jpg");
        // 创建文件输出流，准备写入文件
        FileOutputStream fos =  new FileOutputStream(newFile);
        // 将上传文件的内容写入到新文件中
        fos.write(file.getBytes());
        // 关闭文件输出流
        fos.close();
        // 获取上传文件的原始名称

        // 构造原始文件名对应的文件对象，用于后续删除
        File fileToDelete = new File(filePath + "\\" + originalFilename);
        if (fileToDelete.exists()) {
            // 如果原始文件存在，则删除它
            fileToDelete.delete();
        } else {
            // 如果不存在，抛出文件未找到异常
            throw new FileException(MessageConstant.FILE_NOT_FOUND);
        }

        // 创建访问记录数据传输对象，并设置相关信息
        AccessRecordDTO accessRecordDTO = new AccessRecordDTO();
        accessRecordDTO.setStartTime(String.valueOf(LocalDateTime.now()));
        accessRecordDTO.setLicensePlate(licensePlate);

        ValueOperations ops = redisTemplate.opsForValue();

        ops.set(licensePlate, filePathProperties.getUrl() + "/" + licensePlate + ".jpg");

        // 保存访问记录
        access_recordService.save(accessRecordDTO);
        return filePathProperties.getUrl() + "/" + licensePlate + ".jpg";
    }

    /**
     * 处理上传的文件，包括提取车牌号、保存文件、更新或插入访问记录。
     *
     * @param file 上传的文件，预期为车牌照片。
     */
    @Override
    public String out(MultipartFile file) {
        // 获取文件保存路径
        String filePath = filePathProperties.getPath();
        // 从文件中提取车牌号
        String licensePlate = licensePlate(file);
        String originalFilename = file.getOriginalFilename();
        File originalFile = new File(filePath + "\\" + originalFilename);
        if (originalFile.exists()) {
            // 如果原始文件存在，则删除它
            originalFile.delete();
        }
        // 设置访问记录信息
        AccessRecordDTO accessRecordDTO = new AccessRecordDTO();
        accessRecordDTO.setEndTime(String.valueOf(LocalDateTime.now()));
        accessRecordDTO.setLicensePlate(licensePlate);


        ValueOperations ops = redisTemplate.opsForValue();

        String delete = (String) ops.get("待删除");
        if ( delete != null ){
            File fileToDelete = new File(filePath + "\\" + delete + ".jpg");
            if (fileToDelete.exists()) {
                // 如果文件已存在，则进行删除并更新数据库
                fileToDelete.delete(); // 删除文件
                stringRedisTemplate.delete(licensePlate); // 删除Redis中的记录

            } else {
                // 如果文件不存在，进行新增操作
                access_recordService.save(accessRecordDTO); // 新增数据库访问记录
            }

        }
        access_recordService.update(accessRecordDTO); // 更新数据库访问记录
        ops.set("待删除", licensePlate);
        return filePathProperties.getUrl() + "/" + licensePlate + ".jpg";
    }



    public String licensePlate(MultipartFile file) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(file);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = authService.getAuth();

            String result = HttpUtil.post(url, accessToken, param);
            JSONObject jsonObject = new JSONObject(result);
            String licensePlate = jsonObject.getJSONObject("words_result").getString("number");
            return licensePlate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
