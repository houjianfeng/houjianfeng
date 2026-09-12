package com.plagiarism.checker;

import com.plagiarism.core.NGramTokenizer;
import com.plagiarism.exception.EmptyFileException;
import com.plagiarism.exception.FileReadException;
import com.plagiarism.exception.FileWriteException;
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
 * {@link PaperPlagiarismChecker} 与端到端流程的单元测试。
 *
 * <p>测试思路：使用 @TempDir 构造临时输入/输出文件，
 * 覆盖完全相同文本（重复率 1.0）、完全不同文本（重复率 0.0）、
 * 部分抄袭文本、答案文件格式、异常传播等场景。</p>
 */
@DisplayName("PaperPlagiarismChecker 单元测试")
class PaperPlagiarismCheckerTest {

    private final PaperPlagiarismChecker checker = new PaperPlagiarismChecker();

    private void writeFile(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("用例1: 完全相同的文本重复率为 1.0")
    void testIdenticalFiles(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        writeFile(original, "今天是星期天，天气晴，今天晚上我要去看电影。");
        writeFile(plagiarized, "今天是星期天，天气晴，今天晚上我要去看电影。");

        double similarity = checker.calculateSimilarity(original.toString(), plagiarized.toString());
        assertEquals(1.0, similarity, 1e-9);
    }

    @Test
    @DisplayName("用例2: 完全不同的文本重复率为 0.0")
    void testCompletelyDifferentFiles(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("diff.txt");
        writeFile(original, "苹果香蕉橙子葡萄西瓜");
        writeFile(plagiarized, "飞机火车汽车轮船单车");

        double similarity = checker.calculateSimilarity(original.toString(), plagiarized.toString());
        assertEquals(0.0, similarity, 1e-9);
    }

    @Test
    @DisplayName("用例3: 部分抄袭的文本重复率介于 0 与 1 之间")
    void testPartialPlagiarism(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("add.txt");
        writeFile(original, "今天是星期天，天气晴，今天晚上我要去看电影。");
        writeFile(plagiarized, "今天是周天，天气晴朗，我晚上要去看电影。");

        double similarity = checker.calculateSimilarity(original.toString(), plagiarized.toString());
        assertTrue(similarity > 0.0 && similarity < 1.0,
                "部分抄袭相似度应在 (0,1) 区间，实际: " + similarity);
    }

    @Test
    @DisplayName("用例4: 完整 run 流程将结果写入答案文件，格式为两位小数")
    void testRunWritesResultFile(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        Path output = tempDir.resolve("ans.txt");
        writeFile(original, "今天是星期天，天气晴，今天晚上我要去看电影。");
        writeFile(plagiarized, "今天是星期天，天气晴，今天晚上我要去看电影。");

        checker.run(original.toString(), plagiarized.toString(), output.toString());

        String content = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
        assertEquals("1.00", content);
    }

    @Test
    @DisplayName("用例5: 答案文件输出两位小数（如 0.00）")
    void testOutputFormatTwoDecimals(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("diff.txt");
        Path output = tempDir.resolve("ans.txt");
        writeFile(original, "苹果香蕉橙子");
        writeFile(plagiarized, "飞机火车汽车");

        checker.run(original.toString(), plagiarized.toString(), output.toString());

        String content = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
        assertEquals("0.00", content);
    }

    @Test
    @DisplayName("用例6: 输出文件父目录不存在时自动创建")
    void testRunCreatesParentDirectory(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        Path output = tempDir.resolve("subdir/nested/ans.txt");
        writeFile(original, "测试文本内容样例");
        writeFile(plagiarized, "测试文本内容样例");

        checker.run(original.toString(), plagiarized.toString(), output.toString());

        assertTrue(Files.exists(output));
        assertEquals("1.00", new String(Files.readAllBytes(output), StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("用例7: 输入文件不存在时抛出 FileReadException")
    void testNonExistentInput(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("not_exist.txt");
        FileReadException ex = assertThrows(FileReadException.class,
                () -> checker.calculateSimilarity(nonExistent.toString(), nonExistent.toString()));
        assertTrue(ex.getMessage().contains(nonExistent.toString()));
    }

    @Test
    @DisplayName("用例8: 空输入文件抛出 EmptyFileException")
    void testEmptyInputFile(@TempDir Path tempDir) throws IOException {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("plag.txt");
        Files.write(original, new byte[0]);
        writeFile(plagiarized, "测试内容");

        assertThrows(EmptyFileException.class,
                () -> checker.calculateSimilarity(original.toString(), plagiarized.toString()));
    }

    @Test
    @DisplayName("用例9: 空输出路径抛出 FileWriteException")
    void testEmptyOutputPath(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        writeFile(original, "测试文本内容样例");
        writeFile(plagiarized, "测试文本内容样例");

        assertThrows(FileWriteException.class,
                () -> checker.run(original.toString(), plagiarized.toString(), ""));
    }

    @Test
    @DisplayName("用例10: 自定义 N 值影响结果")
    void testCustomN(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("add.txt");
        writeFile(original, "今天是星期天，天气晴，今天晚上我要去看电影。");
        writeFile(plagiarized, "今天是周天，天气晴朗，我晚上要去看电影。");

        PaperPlagiarismChecker n2Checker = new PaperPlagiarismChecker(new NGramTokenizer(2));
        PaperPlagiarismChecker n3Checker = new PaperPlagiarismChecker(new NGramTokenizer(3));

        double simN2 = n2Checker.calculateSimilarity(original.toString(), plagiarized.toString());
        double simN3 = n3Checker.calculateSimilarity(original.toString(), plagiarized.toString());

        // 较小的 N 对小修改更宽容，相似度应更高
        assertTrue(simN2 > simN3,
                "N=2 相似度应高于 N=3，实际 N2=" + simN2 + " N3=" + simN3);
    }

    @Test
    @DisplayName("用例11: 输出路径指向一个已存在的目录时抛出 FileWriteException")
    void testOutputPathIsDirectory(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        writeFile(original, "测试文本内容样例");
        writeFile(plagiarized, "测试文本内容样例");

        // tempDir 本身是目录，写入它会失败
        assertThrows(FileWriteException.class,
                () -> checker.run(original.toString(), plagiarized.toString(), tempDir.toString()));
    }

    @Test
    @DisplayName("用例12: 输出路径父目录不可写时抛出 FileWriteException")
    void testOutputPathUnwritable(@TempDir Path tempDir) throws Exception {
        Path original = tempDir.resolve("orig.txt");
        Path plagiarized = tempDir.resolve("same.txt");
        writeFile(original, "测试文本内容样例");
        writeFile(plagiarized, "测试文本内容样例");

        // 使用根目录下的不可写路径触发写失败（macOS/Linux 下 /dev/null/x 不存在且不可创建）
        assertThrows(FileWriteException.class,
                () -> checker.run(original.toString(), plagiarized.toString(), "/dev/null/sub/ans.txt"));
    }
}
