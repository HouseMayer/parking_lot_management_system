package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.AccessRecordDTO;
import com.example.dto.PageQueryDTO;
import com.example.dto.RecordPageQueryDTO;
import com.example.entity.AccessRecord;
import com.example.exception.TimeException;
import com.example.mapper.Access_recordMapper;
import com.example.mapper.CarportMapper;
import com.example.mapper.GradeMapper;
import com.example.result.PageResult;
import com.example.service.IAccess_recordService;
import com.example.vo.CarVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
public class Access_recordServiceImpl extends ServiceImpl<Access_recordMapper, AccessRecord> implements IAccess_recordService {

    @Resource
    private Access_recordMapper recordMapper;
    @Resource
    private CarportMapper carportMapper;
    @Resource
    private GradeMapper gradeMapper;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 分页查询记录
     * @param recordPageQueryDTO 记录分页查询DTO对象
     * @return 分页查询结果对象
     */
    @Override
    public PageResult pageQuery(RecordPageQueryDTO recordPageQueryDTO) {

        // 如果参数为空，则抛出运行时异常
        if (recordPageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取当前页面、每页数量和名称
        int currentPage = recordPageQueryDTO.getPage();
        int pageSize = recordPageQueryDTO.getPageSize();
        String keyword = recordPageQueryDTO.getKeyword();


        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper qw = new QueryWrapper();
        if ( !recordPageQueryDTO.getStart().isEmpty() ){
            LocalDateTime start = LocalDateTime.parse(
                    recordPageQueryDTO.getStart(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
            LocalDateTime end = LocalDateTime.parse(
                    recordPageQueryDTO.getEnd(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
            qw.gt("start_time", start);
            qw.lt("start_time", end);
        }
        qw.like("license_plate", keyword);
        qw.orderByDesc("start_time");

        // 创建分页对象
        IPage<AccessRecord> page = new Page<>(currentPage, pageSize);

        // 执行查询，并获取查询结果列表
        IPage<AccessRecord> pageRes = recordMapper.selectPage(page, qw);
        //将数据库内数字转换为意义内容
        pageRes.getRecords().forEach( en -> {
            if (en.getCarport()!= null){
                en.setCarport(carportMapper.getCarportById(en.getCarport()));
            }
        });
        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(pageRes.getRecords());
        log.info("分页查询结果"+pageRes.getRecords().toString());
        pageResult.setTotal(pageRes.getTotal());

        // 返回分页查询结果
        return pageResult;
    }

    /**
     * 保存访问记录
     * @param accessRecordDTO 访问记录DTO对象
     */
    @Override
    public void save(AccessRecordDTO accessRecordDTO) {
        AccessRecord accessRecord = new AccessRecord();

        // 使用BeanUtils将访问记录DTO对象的属性复制到访问记录对象中
        BeanUtils.copyProperties(accessRecordDTO, accessRecord);
        if (accessRecordDTO.getStartTime() != null){
            accessRecord.setStartTime(LocalDateTime.parse(accessRecordDTO.getStartTime()));
        }else {
            accessRecord.setEndTime(LocalDateTime.parse(accessRecordDTO.getEndTime()));
            accessRecord.setCost(BigDecimal.valueOf(0));
        }

        if (accessRecordDTO.getEndTime() != null){
            accessRecord.setEndTime(LocalDateTime.parse(accessRecordDTO.getEndTime()));
        }

        // 设置删除标记为0
        accessRecord.setDeleted(0);
        // 调用记录映射器的插入记录方法，将访问记录插入数据库
        recordMapper.insertRecord(accessRecord);
    }

    /**
     * 更新访问记录。
     * 根据提供的访问记录ID, 更新访问记录的结束时间及计算产生的费用。
     * 费用计算规则基于访问时长，特定车牌等级可能享受优惠。
     *
     * @param accessRecordDTO 包含访问记录ID、结束时间等信息的数据传输对象。
     */
    @Override
    public void update(AccessRecordDTO accessRecordDTO) {
        // 根据车牌号查询对应的访问记录
        List<AccessRecord> recordList = recordMapper.getByLicensePlate(accessRecordDTO.getLicensePlate());
        // 如果未找到访问记录，则直接返回
        if (recordList == null || recordList.isEmpty()){
            return;
        }
        AccessRecord record;
        if (recordList.size() > 1){
            // 寻找开始时间最早的访问记录
            Optional<AccessRecord> max = recordList.stream().max((Comparator.comparing(AccessRecord::getStartTime)));
            record = max.get();
        } else {
            record = recordList.get(0);
        }


        float cost;


        // 获取记录的开始时间
        LocalDateTime startTime = record.getStartTime();

        // 解析结束时间
        LocalDateTime endTime = LocalDateTime.parse(accessRecordDTO.getEndTime());

        // 计算访问时长并根据时长计算费用
        Duration duration = Duration.between(startTime, endTime);
        Long hours = duration.toHours();
        if (hours < 0){
            throw new TimeException(MessageConstant.TIME_ERROR);
        }else if (hours < 2){
            cost = 0;
        } else if (hours < 24) {
            cost = 5 + hours - 2;
            if (cost > 12){
                cost = 12;
            }
        }else {
            cost = hours / 24L + (hours % 24L > 12 ? 12 : hours % 24L);
        }

        // 根据车牌号查询等级并根据等级调整费用
        String grade = gradeMapper.getGradeByLicensePlate(record.getLicensePlate());
        if (grade != null){
            // 如果车牌等级为1，费用设为0
            if ( grade.equals("1")) {
                cost = 0;
            }
        }

        // 设置费用、更新时间、更新用户和结束时间
        record.setCost(BigDecimal.valueOf(cost));
        record.setEndTime(LocalDateTime.parse(accessRecordDTO.getEndTime()));
        record.setUpdateTime(LocalDateTime.now());
        record.setUpdateUser(BaseContext.getCurrentId());
        // 更新访问记录
        recordMapper.updateById(record);
    }

    @Override
    public PageResult pageQueryIn(PageQueryDTO pageQueryDTO) {
        // 如果参数为空，则抛出运行时异常
        if (pageQueryDTO == null) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取当前页面、每页数量和名称
        int currentPage = pageQueryDTO.getPage();
        int pageSize = pageQueryDTO.getPageSize();

        // 创建查询条件封装对象，并根据名称进行模糊查询
        QueryWrapper qw = new QueryWrapper();
        qw.isNull("end_time");
        qw.select("license_plate");
        qw.orderByDesc("start_time");

        // 执行查询，并获取查询结果列表
        List<AccessRecord> carList = recordMapper.selectList(qw);
        List<CarVO> carVOList = new ArrayList<>();
        for (AccessRecord accessRecord : carList) {
            String licensePlate = accessRecord.getLicensePlate();
            String picture = (String) redisTemplate.opsForValue().get(licensePlate);
            carVOList.add(new CarVO(picture, licensePlate));
        }
        // 创建分页查询结果对象
        PageResult pageResult = new PageResult();

        // 设置查询结果记录和总记录数
        pageResult.setRecords(carVOList);
        // 返回分页查询结果
        return pageResult;


    }


}
