package com.plagiarism;

import com.plagiarism.checker.PaperPlagiarismChecker;
import com.plagiarism.exception.FileReadException;
import com.plagiarism.exception.FileWriteException;
import com.plagiarism.exception.InvalidArgumentException;
import com.plagiarism.exception.PlagiarismCheckerException;
import com.plagiarism.exception.EmptyFileException;

/**
 * 程序入口。
 *
 * <p>用法：</p>
 * <pre>
 *   java -jar main.jar &lt;原文文件路径&gt; &lt;抄袭版论文文件路径&gt; &lt;答案文件路径&gt;
 * </pre>
 *
 * <p>程序读取原文与抄袭版论文，计算重复率，将保留两位小数的结果写入答案文件。
 * 所有异常均通过退出码与标准错误输出反馈，避免异常退出。</p>
 */
public final class Main {

    private Main() {
        // 工具类，禁止实例化
    }

    public static void main(String[] args) {
        try {
            run(args);
        } catch (InvalidArgumentException e) {
            System.err.println("[参数错误] " + e.getMessage());
            System.err.println("用法: java -jar main.jar <原文文件> <抄袭版论文> <答案文件>");
            System.exit(2);
        } catch (PlagiarismCheckerException e) {
            System.err.println("[查重错误] " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  原因: " + e.getCause().getMessage());
            }
            System.exit(1);
        }
    }

    /**
     * 解析命令行参数并执行查重流程。
     *
     * @param args 命令行参数
     * @throws InvalidArgumentException    参数个数或内容非法
     * @throws FileReadException          读取输入文件失败
     * @throws EmptyFileException         输入文件内容为空
     * @throws FileWriteException         写入答案文件失败
     */
    static void run(String[] args)
            throws InvalidArgumentException, FileReadException, EmptyFileException, FileWriteException {
        if (args == null || args.length != 3) {
            int count = args == null ? 0 : args.length;
            throw new InvalidArgumentException(
                    "需要 3 个参数（原文文件、抄袭版论文、答案文件），实际给出: " + count);
        }

        String originalPath = args[0];
        String plagiarizedPath = args[1];
        String outputPath = args[2];

        if (originalPath.trim().isEmpty() || plagiarizedPath.trim().isEmpty() || outputPath.trim().isEmpty()) {
            throw new InvalidArgumentException("文件路径不能为空字符串");
        }

        PaperPlagiarismChecker checker = new PaperPlagiarismChecker();
        checker.run(originalPath, plagiarizedPath, outputPath);
    }
}
