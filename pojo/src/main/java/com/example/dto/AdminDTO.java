package com.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminDTO implements Serializable {

    private Long id;

    //姓名
    private String name;

    //用户名
    private String userName;

    private String password;

    //电话号码
    private String phone;

    //角色
    private Integer role;

}
