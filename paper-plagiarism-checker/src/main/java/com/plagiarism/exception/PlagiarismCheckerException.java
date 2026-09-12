package com.plagiarism.exception;

/**
 * 论文查重工具的异常基类。
 * 所有自定义异常均继承自此基类，便于调用方统一捕获与处理。
 */
public class PlagiarismCheckerException extends Exception {

    private static final long serialVersionUID = 1L;

    public PlagiarismCheckerException(String message) {
        super(message);
    }

    public PlagiarismCheckerException(String message, Throwable cause) {
        super(message, cause);
    }
}
