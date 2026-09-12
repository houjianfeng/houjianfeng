package com.plagiarism.exception;

/**
 * 当读取输入文件失败时抛出。
 * 对应场景：文件不存在、文件不可读、编码异常等。
 */
public class FileReadException extends PlagiarismCheckerException {

    private static final long serialVersionUID = 1L;

    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
