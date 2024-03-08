package com.example.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUploadService {


    void in(MultipartFile file) throws IOException;

    void out(MultipartFile file);
}
