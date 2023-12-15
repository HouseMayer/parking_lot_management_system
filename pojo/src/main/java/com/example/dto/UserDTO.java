package com.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private String name;

    private String username;

    private String phone;

    private String role;

}
