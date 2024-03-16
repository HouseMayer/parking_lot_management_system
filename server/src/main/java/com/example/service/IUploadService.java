package com.example.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUploadService {


    String in(MultipartFile file) throws IOException;

    String out(MultipartFile file);
}
