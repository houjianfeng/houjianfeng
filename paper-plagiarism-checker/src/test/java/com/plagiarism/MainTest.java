package com.plagiarism;

import com.plagiarism.exception.EmptyFileException;
import com.plagiarism.exception.FileReadException;
import com.plagiarism.exception.InvalidArgumentException;
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
 * {@link Main} 入口的单元测试。
 *
 * <p>测试思路：覆盖参数个数校验、空参数、完整流程成功等场景。
 * Main.run 是包级可见的静态方法，可直接调用以避免子进程开销。</p>
 */
@DisplayName("Main 入口单元测试")
class MainTest {

    private void writeFile(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("用例1: 参数个数不足抛出 InvalidArgumentException")
    void testTooFewArgs() {
        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> Main.run(new String[]{"only_one.txt"}));
        assertTrue(ex.getMessage().contains("3"));
    }

    @Test
    @DisplayName("用例2: 参数过多抛出 InvalidArgumentException")
    void testTooManyArgs() {
        assertThrows(InvalidArgumentException.class,
                () -> Main.run(new String[]{"a.txt", "b.txt", "c.txt", "d.txt"}));
    }

    @Test
    @DisplayName("用例3: null 参数抛出 InvalidArgumentException")
    void testNullArgs() {
        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> Main.run(null));
        assertTrue(ex.getMessage().contains("3"));
    }

    @Test
    @DisplayName("用例4: 空字符串参数抛出 InvalidArgumentException")
    void testBlankArg(@TempDir Path tempDir) throws IOException {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        writeFile(original, "测试文本");
        writeFile(plagiarized, "测试文本");

        assertThrows(InvalidArgumentException.class,
                () -> Main.run(new String[]{original.toString(), plagiarized.toString(), "   "}));
    }

    @Test
    @DisplayName("用例5: 完整流程成功运行并写出答案文件")
    void testFullRunSuccess(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        Path output = tempDir.resolve("ans.txt");
        writeFile(original, "今天是星期天，天气晴，今天晚上我要去看电影。");
        writeFile(plagiarized, "今天是星期天，天气晴，今天晚上我要去看电影。");

        Main.run(new String[]{original.toString(), plagiarized.toString(), output.toString()});

        String content = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
        assertEquals("1.00", content);
    }

    @Test
    @DisplayName("用例6: 输入文件不存在时抛出 FileReadException")
    void testNonExistentInput(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("not_exist.txt");
        Path output = tempDir.resolve("ans.txt");

        assertThrows(FileReadException.class,
                () -> Main.run(new String[]{nonExistent.toString(), nonExistent.toString(), output.toString()}));
    }

    @Test
    @DisplayName("用例7: 空输入文件抛出 EmptyFileException")
    void testEmptyInput(@TempDir Path tempDir) throws IOException {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        Path output = tempDir.resolve("ans.txt");
        Files.write(original, new byte[0]);
        writeFile(plagiarized, "测试内容");

        assertThrows(EmptyFileException.class,
                () -> Main.run(new String[]{original.toString(), plagiarized.toString(), output.toString()}));
    }
}
