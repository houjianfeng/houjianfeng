package com.plagiarism.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 字符级 N-gram 分词器。
 *
 * <p>将预处理后的文本按滑动窗口切成长度为 N 的字符片段（N-gram），
 * 并统计每个 N-gram 的出现频次，形成词频（TF）向量。
 * 字符级 N-gram 无需中文分词库，且对词序敏感，适合论文查重场景。</p>
 *
 * <p>算法关键点：</p>
 * <ul>
 *   <li>采用滑动窗口，窗口大小为 N，步长为 1；</li>
 *   <li>当文本长度小于 N 时，退化为整段文本作为一个 token，保证短文本也能计算；</li>
 *   <li>返回不可修改的词频 Map，避免外部篡改。</li>
 * </ul>
 */
public class NGramTokenizer {

    /** 默认 N-gram 长度，3 在中文论文查重中能较好兼顾局部词序与抗小修改能力。 */
    public static final int DEFAULT_N = 3;

    private final int n;

    /**
     * 构造指定 N 值的分词器。
     *
     * @param n N-gram 长度，必须为正整数
     * @throws IllegalArgumentException 当 n &lt;= 0 时抛出
     */
    public NGramTokenizer(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N-gram length must be positive, but got: " + n);
        }
        this.n = n;
    }

    /** 使用默认 N=3 构造分词器。 */
    public NGramTokenizer() {
        this(DEFAULT_N);
    }

    /**
     * 对预处理后的文本生成 N-gram 词频向量。
     *
     * @param text 已去除空白等噪声的文本，不能为 null
     * @return 不可修改的 N-gram -&gt; 频次 映射
     * @throws IllegalArgumentException 当 text 为 null 时抛出
     */
    public Map<String, Integer> tokenize(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }

        Map<String, Integer> frequency = new HashMap<>();
        int length = text.length();
        if (length == 0) {
            return Collections.unmodifiableMap(frequency);
        }

        // 文本长度不足 N 时，整段作为一个 token，避免短文本无法生成特征
        if (length < n) {
            frequency.merge(text, 1, Integer::sum);
            return Collections.unmodifiableMap(frequency);
        }

        // 滑动窗口生成 N-gram，使用 codePoint 方式以正确处理 Unicode 代理对
        int codePointCount = text.codePointCount(0, length);
        if (codePointCount < n) {
            // codePoint 数量不足 N，退化为整段
            frequency.merge(text, 1, Integer::sum);
            return Collections.unmodifiableMap(frequency);
        }

        int index = 0;
        while (index <= length - n) {
            String gram = text.substring(index, index + n);
            frequency.merge(gram, 1, Integer::sum);
            index++;
        }

        return Collections.unmodifiableMap(frequency);
    }

    /** @return 当前分词器使用的 N 值。 */
    public int getN() {
        return n;
    }
}
