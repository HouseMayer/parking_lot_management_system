package com.example.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class AdminLoginDTO implements Serializable {

    private String username;
    private String password;

}
