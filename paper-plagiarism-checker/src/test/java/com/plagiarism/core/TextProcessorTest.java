package com.plagiarism.core;

import com.plagiarism.exception.EmptyFileException;
import com.plagiarism.exception.FileReadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TextProcessor} 的单元测试。
 *
 * <p>测试思路：使用 JUnit 5 的 @TempDir 创建临时文件，
 * 覆盖正常读取、文件不存在、空文件、预处理去空白等场景。</p>
 */
@DisplayName("TextProcessor 单元测试")
class TextProcessorTest {

    private final TextProcessor processor = new TextProcessor();

    @Test
    @DisplayName("用例1: 正常读取 UTF-8 中文文件并去除空白")
    void testReadNormalFile(@TempDir Path tempDir) throws IOException, EmptyFileException, FileReadException {
        Path file = tempDir.resolve("input.txt");
        Files.write(file, "今天是 星期天\n天气晴".getBytes(StandardCharsets.UTF_8));

        String result = processor.readAndPreprocess(file.toString());
        assertEquals("今天是星期天天气晴", result);
    }

    @Test
    @DisplayName("用例2: 读取不存在的文件抛出 FileReadException")
    void testReadNonExistentFile(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("not_exist.txt");
        FileReadException ex = assertThrows(FileReadException.class,
                () -> processor.readAndPreprocess(nonExistent.toString()));
        assertTrue(ex.getMessage().contains(nonExistent.toString()));
    }

    @Test
    @DisplayName("用例3: 空文件抛出 EmptyFileException")
    void testEmptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.write(file, new byte[0]);

        assertThrows(EmptyFileException.class, () -> processor.readAndPreprocess(file.toString()));
    }

    @Test
    @DisplayName("用例4: 仅含空白的文件抛出 EmptyFileException")
    void testWhitespaceOnlyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("whitespace.txt");
        Files.write(file, "   \n\t  \r\n".getBytes(StandardCharsets.UTF_8));

        assertThrows(EmptyFileException.class, () -> processor.readAndPreprocess(file.toString()));
    }

    @Test
    @DisplayName("用例5: preprocess 去除所有空白字符")
    void testPreprocessRemovesWhitespace() {
        String input = "a b\tc\nd\re";
        String result = processor.preprocess(input);
        assertEquals("abcde", result);
    }

    @Test
    @DisplayName("用例6: preprocess 保留标点符号")
    void testPreprocessKeepsPunctuation() {
        String input = "今天，天气晴！";
        String result = processor.preprocess(input);
        assertEquals("今天，天气晴！", result);
    }

    @Test
    @DisplayName("用例7: preprocess 对 null 返回空串")
    void testPreprocessNull() {
        assertEquals("", processor.preprocess(null));
    }

    @Test
    @DisplayName("用例8: 空路径抛出 FileReadException")
    void testEmptyPath() {
        assertThrows(FileReadException.class, () -> processor.readAndPreprocess(""));
        assertThrows(FileReadException.class, () -> processor.readAndPreprocess("   "));
    }
}
