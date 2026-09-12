package com.plagiarism.exception;

/**
 * 当写入答案文件失败时抛出。
 * 对应场景：输出路径不可写、磁盘空间不足、父目录不存在等。
 */
public class FileWriteException extends PlagiarismCheckerException {

    private static final long serialVersionUID = 1L;

    public FileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
