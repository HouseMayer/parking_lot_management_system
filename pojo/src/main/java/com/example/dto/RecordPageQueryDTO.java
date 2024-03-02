package com.example.dto;

import lombok.Data;

@Data
public class RecordPageQueryDTO {

    //关键字
    private String keyword;

    //开始时间
    private String start;

    //结束时间
    private String end;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
