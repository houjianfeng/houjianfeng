package com.plagiarism.exception;

/**
 * 当输入文件经预处理后文本为空时抛出。
 * 对应场景：原文或抄袭版论文文件内容为空白、仅含空白字符，
 * 此时无法生成有效的 N-gram 特征向量，查重无意义。
 */
public class EmptyFileException extends PlagiarismCheckerException {

    private static final long serialVersionUID = 1L;

    public EmptyFileException(String message) {
        super(message);
    }
}
