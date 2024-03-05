package com.example.dto;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Data
public class BatchExportDTO {

    List<Long> ids;

    HttpServletResponse response;
}

