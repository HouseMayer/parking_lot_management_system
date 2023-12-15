package com.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private String name;

    private String userName;

    private String phone;

    private Integer role;

}
