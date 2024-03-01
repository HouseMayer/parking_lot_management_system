package com.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeDTO {

    private Long id;

    private String licensePlate;

    private String owner;

    private String grade;

    private LocalDateTime deadline;

    private String phone;

    private String comment;
}
