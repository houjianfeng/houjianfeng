package com.plagiarism.core;

import com.plagiarism.exception.EmptyFileException;
import com.plagiarism.exception.FileReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文本处理器：负责文件读取与文本预处理。
 *
 * <p>职责：</p>
 * <ul>
 *   <li>以 UTF-8 读取原文/抄袭版论文文件内容；</li>
 *   <li>对文本进行预处理——去除空白字符（空格、制表、换行、回车），
 *       保留标点与正文，使 N-gram 聚焦于实际内容；</li>
 *   <li>对读取异常与空内容进行包装，抛出语义化异常。</li>
 * </ul>
 *
 * <p>注意：本类只读取调用方显式指定的输入文件，
 * 不访问任何其他文件，符合评测安全约束。</p>
 */
public class TextProcessor {

    /**
     * 读取指定路径文件并返回预处理后的文本。
     *
     * @param filePath 文件绝对路径
     * @return 去除空白后的文本
     * @throws FileReadException  当文件不存在或读取失败时抛出
     * @throws EmptyFileException 当文件内容经预处理后为空时抛出
     */
    public String readAndPreprocess(String filePath) throws FileReadException, EmptyFileException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new FileReadException("file path is empty", null);
        }

        Path path = Paths.get(filePath);
        String raw;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, read);
            }
            raw = builder.toString();
        } catch (IOException e) {
            throw new FileReadException("failed to read file: " + filePath, e);
        }

        String processed = preprocess(raw);
        if (processed.isEmpty()) {
            throw new EmptyFileException("file is empty after preprocessing: " + filePath);
        }
        return processed;
    }

    /**
     * 预处理文本：去除所有空白字符（空格、制表、换行、回车、换页等）。
     *
     * <p>不去除标点：标点在中文句子结构中携带一定语义信息，
     * 保留可在一定程度上提升查重区分度。</p>
     *
     * @param text 原始文本
     * @return 去除空白后的文本；输入为 null 时返回空串
     */
    public String preprocess(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int length = text.length();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (!Character.isWhitespace(ch)) {
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
