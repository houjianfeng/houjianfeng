package com.plagiarism.core;

import java.util.Map;

/**
 * 余弦相似度计算器。
 *
 * <p>基于两个词频向量计算余弦相似度：
 * <pre>
 *   cos(a, b) = (a · b) / (||a|| * ||b||)
 * </pre>
 * 结果范围为 [0, 1]（词频非负，故无负向夹角）。</p>
 *
 * <p>性能要点：仅遍历较小的向量，以它的 key 在较大向量中查询，
 * 将复杂度从 O(|A|*|B|) 降至 O(min(|A|,|B|))。</p>
 */
public class SimilarityCalculator {

    /**
     * 计算两个词频向量的余弦相似度。
     *
     * @param vectorA 第一个词频向量，不能为 null
     * @param vectorB 第二个词频向量，不能为 null
     * @return 余弦相似度，范围 [0.0, 1.0]；当两向量均为空时返回 0.0
     * @throws IllegalArgumentException 当任一向量为 null 时抛出
     */
    public double cosineSimilarity(Map<String, Integer> vectorA, Map<String, Integer> vectorB) {
        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("vectors must not be null");
        }

        // 选取较小的向量作为外层循环，降低遍历开销
        Map<String, Integer> smaller = vectorA.size() <= vectorB.size() ? vectorA : vectorB;
        Map<String, Integer> larger = vectorA.size() <= vectorB.size() ? vectorB : vectorA;

        // 计算点积：仅对两向量共有的 key 求频次乘积之和
        double dotProduct = 0.0;
        for (Map.Entry<String, Integer> entry : smaller.entrySet()) {
            Integer other = larger.get(entry.getKey());
            if (other != null) {
                dotProduct += (double) entry.getValue() * (double) other;
            }
        }

        if (dotProduct == 0.0) {
            return 0.0;
        }

        double normA = norm(vectorA);
        double normB = norm(vectorB);
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (normA * normB);
    }

    /**
     * 计算向量的 L2 范数。
     *
     * @param vector 词频向量
     * @return L2 范数；空向量返回 0.0
     */
    private double norm(Map<String, Integer> vector) {
        double sum = 0.0;
        for (int freq : vector.values()) {
            sum += (double) freq * (double) freq;
        }
        return Math.sqrt(sum);
    }
}
