package com.example.dto;


import lombok.Data;

@Data
public class PageQueryDTO {

    //关键字
    private String keyword;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
