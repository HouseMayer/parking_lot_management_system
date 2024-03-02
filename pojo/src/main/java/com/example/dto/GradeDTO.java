package com.example.dto;

import lombok.Data;


@Data
public class GradeDTO {

    private Long id;

    private String licensePlate;

    private String owner;

    private String grade;

    private String deadline;

    private String phone;

    private String comment;
}
