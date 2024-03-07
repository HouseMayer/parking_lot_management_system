package com.example.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 文件读取工具类
 */
public class FileUtil {

    /**
     * 读取文件内容，作为字符串返回
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } 

        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        } 

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流  
        FileInputStream fis = new FileInputStream(filePath);  
        // 创建一个长度为10240的Buffer
        byte[] bbuf = new byte[10240];  
        // 用于保存实际读取的字节数  
        int hasRead = 0;  
        while ( (hasRead = fis.read(bbuf)) > 0 ) {  
            sb.append(new String(bbuf, 0, hasRead));  
        }  
        fis.close();  
        return sb.toString();
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }


    /**
     * 读取并返回文件的内容。
     *
     * @param multipartFile 表示要读取的文件，类型为MultipartFile。
     * @return 文件的内容，以字符串形式返回。
     * @throws IOException 如果文件不存在或文件过大，则抛出此异常。
     * @throws FileNotFoundException 如果文件不存在，则抛出此异常。
     */
    public static String readFileAsString(MultipartFile multipartFile) throws IOException {
        File file= (File) multipartFile;
        // 检查文件是否存在
        if (!file.exists()) {
            throw new FileNotFoundException("图片未获取");
        }

        // 检查文件大小是否超过1GB
        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        }

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流用于读取文件
        FileInputStream fis = new FileInputStream(file);
        // 创建一个缓冲区，用于存储读取的字节
        byte[] bbuf = new byte[10240];
        // hasRead用于保存每次读取的字节数
        int hasRead = 0;
        // 循环读取文件，直到没有更多的字节可读
        while ( (hasRead = fis.read(bbuf)) > 0 ) {
            // 将读取的字节转换为字符串，并追加到StringBuilder中
            sb.append(new String(bbuf, 0, hasRead));
        }
        // 关闭文件输入流
        fis.close();
        return sb.toString();
    }


    public static byte[] readFileByBytes(MultipartFile multipartFile) throws IOException {
        File file= (File) multipartFile;
        if (!file.exists()) {
            throw new FileNotFoundException("图片未获取");
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }


}
