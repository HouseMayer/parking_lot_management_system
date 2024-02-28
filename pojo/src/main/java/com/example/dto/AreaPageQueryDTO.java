package com.example.dto;


import lombok.Data;

@Data
public class AreaPageQueryDTO {

    //区域名称
    private String keyword;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
