package com.example.dto;

import lombok.Data;

@Data
public class AccessRecordDTO {

    private Long id;

    //拍照
    private String licensePlate;

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;

    //车位
    private String carport;

    //费用
    private String cost;
}
