package com.example.exception;

/**
 * 账号被锁定异常
 */
public class FileException extends BaseException {

    public FileException() {
    }

    public FileException(String msg) {
        super(msg);
    }

}
