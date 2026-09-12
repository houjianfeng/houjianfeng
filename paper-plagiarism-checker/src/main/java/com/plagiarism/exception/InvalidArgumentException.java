package com.plagiarism.exception;

/**
 * 当命令行参数个数不正确或参数内容非法时抛出。
 * 对应场景：未提供 3 个文件路径参数、参数为空字符串等。
 */
public class InvalidArgumentException extends PlagiarismCheckerException {

    private static final long serialVersionUID = 1L;

    public InvalidArgumentException(String message) {
        super(message);
    }
}
