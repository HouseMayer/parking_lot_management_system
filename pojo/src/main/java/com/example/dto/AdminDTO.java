package com.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminDTO implements Serializable {

    private Long id;

    private String name;

    private String userName;

    private String phone;

    private Integer role;

}
