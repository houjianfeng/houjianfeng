package com.plagiarism.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link NGramTokenizer} 的单元测试。
 *
 * <p>测试思路（白盒）：覆盖正常路径、短文本退化路径、空文本、null 输入、非法 N 值等分支。</p>
 */
@DisplayName("NGramTokenizer 单元测试")
class NGramTokenizerTest {

    @Test
    @DisplayName("用例1: 长度足够的文本按 N=3 滑动窗口正确分词")
    void testNormalTokenization() {
        NGramTokenizer tokenizer = new NGramTokenizer(3);
        Map<String, Integer> result = tokenizer.tokenize("abcde");

        // 期望 5-3+1 = 3 个 N-gram：abc, bcd, cde
        assertEquals(3, result.size());
        assertEquals(1, result.get("abc"));
        assertEquals(1, result.get("bcd"));
        assertEquals(1, result.get("cde"));
    }

    @Test
    @DisplayName("用例2: 文本长度恰好等于 N 时产生 1 个 N-gram")
    void testTextLengthEqualsN() {
        NGramTokenizer tokenizer = new NGramTokenizer(3);
        Map<String, Integer> result = tokenizer.tokenize("abc");

        assertEquals(1, result.size());
        assertEquals(1, result.get("abc"));
    }

    @Test
    @DisplayName("用例3: 文本长度小于 N 时退化为整段文本")
    void testTextShorterThanN() {
        NGramTokenizer tokenizer = new NGramTokenizer(5);
        Map<String, Integer> result = tokenizer.tokenize("abc");

        // 短文本退化为整段
        assertEquals(1, result.size());
        assertEquals(1, result.get("abc"));
    }

    @Test
    @DisplayName("用例4: 重复 N-gram 正确累计频次")
    void testRepeatedGrams() {
        NGramTokenizer tokenizer = new NGramTokenizer(2);
        Map<String, Integer> result = tokenizer.tokenize("ababab");

        // bigrams: ab, ba, ab, ba, ab -> ab:3, ba:2
        assertEquals(3, result.get("ab"));
        assertEquals(2, result.get("ba"));
    }

    @Test
    @DisplayName("用例5: 中文文本 N-gram 生成正确")
    void testChineseText() {
        NGramTokenizer tokenizer = new NGramTokenizer(2);
        Map<String, Integer> result = tokenizer.tokenize("今天是周天");

        // 4 个 bigram: 今天, 天是, 是周, 周天
        assertEquals(4, result.size());
        assertTrue(result.containsKey("今天"));
        assertTrue(result.containsKey("周天"));
    }

    @Test
    @DisplayName("用例6: 空文本返回空 Map")
    void testEmptyText() {
        NGramTokenizer tokenizer = new NGramTokenizer(3);
        Map<String, Integer> result = tokenizer.tokenize("");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("用例7: null 输入抛出 IllegalArgumentException")
    void testNullText() {
        NGramTokenizer tokenizer = new NGramTokenizer(3);
        assertThrows(IllegalArgumentException.class, () -> tokenizer.tokenize(null));
    }

    @Test
    @DisplayName("用例8: 返回的 Map 不可修改")
    void testUnmodifiableMap() {
        NGramTokenizer tokenizer = new NGramTokenizer(3);
        Map<String, Integer> result = tokenizer.tokenize("abcde");

        assertThrows(UnsupportedOperationException.class, () -> result.put("new", 1));
    }

    @Test
    @DisplayName("用例9: N<=0 时构造抛出 IllegalArgumentException")
    void testInvalidN() {
        assertThrows(IllegalArgumentException.class, () -> new NGramTokenizer(0));
        assertThrows(IllegalArgumentException.class, () -> new NGramTokenizer(-1));
    }

    @Test
    @DisplayName("用例10: 默认构造使用 N=3")
    void testDefaultN() {
        NGramTokenizer tokenizer = new NGramTokenizer();
        assertEquals(NGramTokenizer.DEFAULT_N, tokenizer.getN());
    }

    @Test
    @DisplayName("用例11: 返回 Map 不含未出现文本的 key")
    void testNoExtraKeys() {
        NGramTokenizer tokenizer = new NGramTokenizer(3);
        Map<String, Integer> result = tokenizer.tokenize("abcde");

        assertFalse(result.containsKey("xyz"));
    }
}
