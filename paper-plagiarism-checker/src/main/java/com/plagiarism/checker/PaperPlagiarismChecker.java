package com.plagiarism.checker;

import com.plagiarism.core.NGramTokenizer;
import com.plagiarism.core.SimilarityCalculator;
import com.plagiarism.core.TextProcessor;
import com.plagiarism.exception.EmptyFileException;
import com.plagiarism.exception.FileReadException;
import com.plagiarism.exception.FileWriteException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

/**
 * 论文查重编排器：串联文本读取、N-gram 分词、相似度计算与结果输出。
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>读取并预处理原文与抄袭版论文；</li>
 *   <li>对两段文本分别生成 N-gram 词频向量；</li>
 *   <li>计算余弦相似度，得到重复率；</li>
 *   <li>将结果格式化为保留两位小数的浮点数写入答案文件。</li>
 * </ol>
 *
 * <p>该类只读取原文/抄袭版输入文件，只写入指定的答案文件，
 * 严格遵守评测安全约束。</p>
 */
public class PaperPlagiarismChecker {

    private final TextProcessor textProcessor;
    private final NGramTokenizer tokenizer;
    private final SimilarityCalculator similarityCalculator;

    /**
     * 使用默认 N=3 构造查重器。
     */
    public PaperPlagiarismChecker() {
        this(new NGramTokenizer());
    }

    /**
     * 使用指定 N 值构造查重器。
     *
     * @param tokenizer 已配置好 N 值的分词器
     */
    public PaperPlagiarismChecker(NGramTokenizer tokenizer) {
        this.textProcessor = new TextProcessor();
        this.tokenizer = tokenizer;
        this.similarityCalculator = new SimilarityCalculator();
    }

    /**
     * 计算两份论文的重复率（0.0~1.0）。
     *
     * @param originalPath    原文文件路径
     * @param plagiarizedPath 抄袭版论文文件路径
     * @return 重复率，范围 [0.0, 1.0]
     * @throws FileReadException 当文件读取失败时抛出
     * @throws EmptyFileException 当文件内容为空时抛出
     */
    public double calculateSimilarity(String originalPath, String plagiarizedPath)
            throws FileReadException, EmptyFileException {
        String originalText = textProcessor.readAndPreprocess(originalPath);
        String plagiarizedText = textProcessor.readAndPreprocess(plagiarizedPath);

        Map<String, Integer> originalVector = tokenizer.tokenize(originalText);
        Map<String, Integer> plagiarizedVector = tokenizer.tokenize(plagiarizedText);

        return similarityCalculator.cosineSimilarity(originalVector, plagiarizedVector);
    }

    /**
     * 执行完整的查重流程并将结果写入答案文件。
     *
     * <p>答案格式：保留两位小数的浮点数，如 {@code 0.87}。</p>
     *
     * @param originalPath    原文文件路径
     * @param plagiarizedPath 抄袭版论文文件路径
     * @param outputPath      答案文件路径
     * @throws FileReadException  读取输入失败
     * @throws EmptyFileException 输入内容为空
     * @throws FileWriteException 写入答案失败
     */
    public void run(String originalPath, String plagiarizedPath, String outputPath)
            throws FileReadException, EmptyFileException, FileWriteException {
        double similarity = calculateSimilarity(originalPath, plagiarizedPath);
        writeResult(outputPath, similarity);
    }

    /**
     * 将重复率写入答案文件，保留两位小数。
     *
     * @param outputPath  答案文件路径
     * @param similarity  重复率
     * @throws FileWriteException 写入失败
     */
    private void writeResult(String outputPath, double similarity) throws FileWriteException {
        if (outputPath == null || outputPath.trim().isEmpty()) {
            throw new FileWriteException("output path is empty", null);
        }

        String result = String.format(Locale.US, "%.2f", similarity);
        Path path = Paths.get(outputPath);

        // 确保父目录存在
        Path parent = path.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new FileWriteException("failed to create parent directory: " + parent, e);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(result);
        } catch (IOException e) {
            throw new FileWriteException("failed to write result to: " + outputPath, e);
        }
    }
}
