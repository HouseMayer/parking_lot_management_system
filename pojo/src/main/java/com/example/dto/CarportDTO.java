package com.example.dto;

import lombok.Data;

@Data
public class CarportDTO {

    private Long id;

    //车位号
    private String carport;

    //区域
    private String area;

    //车位类型：1.露天2室内
    private String type;

    //车位分类：1.固定车位2.临时车位
    private String classify;

    //车位分类：1.固定车位2.临时车位
    private String licensePlate;

    //状态：1.空闲 2.占用3.维修
    private String state;



}

