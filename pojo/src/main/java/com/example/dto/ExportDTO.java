package com.example.dto;
import javax.servlet.http.HttpServletResponse;

import lombok.Data;

@Data
public class ExportDTO {

    String start;
    String end;
    HttpServletResponse response;
}
