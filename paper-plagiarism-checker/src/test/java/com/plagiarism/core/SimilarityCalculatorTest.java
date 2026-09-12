package com.plagiarism.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link SimilarityCalculator} 的单元测试。
 *
 * <p>测试思路：覆盖完全相同、完全不相交、部分重叠、空向量、null 输入等场景，
 * 验证余弦相似度的边界与典型值。</p>
 */
@DisplayName("SimilarityCalculator 单元测试")
class SimilarityCalculatorTest {

    private final SimilarityCalculator calculator = new SimilarityCalculator();

    private static Map<String, Integer> map(Object... pairs) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            m.put((String) pairs[i], (Integer) pairs[i + 1]);
        }
        return m;
    }

    @Test
    @DisplayName("用例1: 完全相同的向量相似度为 1.0")
    void testIdenticalVectors() {
        Map<String, Integer> a = map("abc", 2, "def", 3);
        assertEquals(1.0, calculator.cosineSimilarity(a, a), 1e-9);
    }

    @Test
    @DisplayName("用例2: 完全不相交的向量相似度为 0.0")
    void testDisjointVectors() {
        Map<String, Integer> a = map("abc", 1, "def", 1);
        Map<String, Integer> b = map("ghi", 1, "jkl", 1);
        assertEquals(0.0, calculator.cosineSimilarity(a, b), 1e-9);
    }

    @Test
    @DisplayName("用例3: 部分重叠的向量相似度介于 0 与 1 之间")
    void testPartialOverlap() {
        // a={abc:1, def:1}, b={abc:1, xyz:1}
        // dot=1, |a|=sqrt(2), |b|=sqrt(2), cos=1/2=0.5
        Map<String, Integer> a = map("abc", 1, "def", 1);
        Map<String, Integer> b = map("abc", 1, "xyz", 1);
        assertEquals(0.5, calculator.cosineSimilarity(a, b), 1e-9);
    }

    @Test
    @DisplayName("用例4: 一个向量为空时相似度为 0.0")
    void testEmptyVector() {
        Map<String, Integer> a = map("abc", 1);
        Map<String, Integer> empty = new HashMap<>();
        assertEquals(0.0, calculator.cosineSimilarity(a, empty), 1e-9);
    }

    @Test
    @DisplayName("用例5: 两个空向量相似度为 0.0（避免除零）")
    void testBothEmpty() {
        Map<String, Integer> empty1 = new HashMap<>();
        Map<String, Integer> empty2 = new HashMap<>();
        assertEquals(0.0, calculator.cosineSimilarity(empty1, empty2), 1e-9);
    }

    @Test
    @DisplayName("用例6: null 输入抛出 IllegalArgumentException")
    void testNullInput() {
        Map<String, Integer> a = map("abc", 1);
        assertThrows(IllegalArgumentException.class, () -> calculator.cosineSimilarity(null, a));
        assertThrows(IllegalArgumentException.class, () -> calculator.cosineSimilarity(a, null));
    }

    @Test
    @DisplayName("用例7: 频次加权的余弦相似度正确")
    void testWeightedSimilarity() {
        // a={x:1,y:1}, b={x:2,y:0}
        // dot=1*2 + 1*0=2, |a|=sqrt(2), |b|=2, cos=2/(2*sqrt(2))=1/sqrt(2)≈0.7071
        Map<String, Integer> a = map("x", 1, "y", 1);
        Map<String, Integer> b = map("x", 2, "z", 0);
        // 注意：z=0 不影响 norm
        double expected = 1.0 / Math.sqrt(2);
        assertEquals(expected, calculator.cosineSimilarity(a, b), 1e-9);
    }

    @Test
    @DisplayName("用例8: 较大向量在第一参数位置时也能正确计算（验证对称性）")
    void testSymmetry() {
        Map<String, Integer> a = map("a", 1, "b", 2, "c", 3);
        Map<String, Integer> b = map("a", 1, "d", 1);
        double simAB = calculator.cosineSimilarity(a, b);
        double simBA = calculator.cosineSimilarity(b, a);
        assertEquals(simAB, simBA, 1e-9);
    }
}
