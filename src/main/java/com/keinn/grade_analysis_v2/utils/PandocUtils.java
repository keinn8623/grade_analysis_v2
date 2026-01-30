package com.keinn.grade_analysis_v2.utils;





import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class PandocUtils {

    /**
     * 使用 pandoc 将 docx 文件转换为 markdown
     * @param inputPath 输入的 docx 文件路径
     * @param outputPath 输出的 markdown 文件路径
     * @throws IOException 如果文件操作失败或 pandoc 命令执行失败
     * @throws InterruptedException 如果命令执行被中断
     */
    public static void convertDocxToMarkdown(String inputPath, String outputPath) throws IOException, InterruptedException {
        convertDocxToMarkdown(inputPath, outputPath, null); // 默认不处理图片
    }

    /**
     * 使用 pandoc 将 docx 文件转换为 markdown，并可指定图片输出目录
     * @param inputPath 输入的 docx 文件路径
     * @param outputPath 输出的 markdown 文件路径
     * @param imageOutputDir 图片输出目录，如果为 null 则不提取图片
     * @throws IOException 如果文件操作失败或 pandoc 命令执行失败
     * @throws InterruptedException 如果命令执行被中断
     */
    public static void convertDocxToMarkdown(String inputPath, String outputPath, String imageOutputDir) throws IOException, InterruptedException {
        // 验证输入文件是否存在
        Path input = Paths.get(inputPath);
        if (!Files.exists(input)) {
            throw new IOException("输入文件不存在: " + inputPath);
        }

        // 检查输入文件是否为 .docx 格式
        if (!inputPath.toLowerCase().endsWith(".docx")) {
            throw new IllegalArgumentException("输入文件必须是 .docx 格式");
        }

        // 确保输出目录存在
        Path output = Paths.get(outputPath);
        Files.createDirectories(output.getParent());

        // 构建 pandoc 命令
        ProcessBuilder processBuilder;
        if (imageOutputDir != null && !imageOutputDir.trim().isEmpty()) {
            // 创建图片输出目录
            Path imageDir = Paths.get(imageOutputDir);
            Files.createDirectories(imageDir);

            // 使用 --extract-media 参数提取图片
            processBuilder = new ProcessBuilder(
                    "pandoc",
                    "-f", "docx",
                    "-t", "markdown",
                    "--wrap=none",  // 不自动换行
                    "--extract-media=" + imageOutputDir,  // 提取媒体文件到指定目录
                    inputPath,
                    "-o", outputPath
            );
        } else {
            processBuilder = new ProcessBuilder(
                    "pandoc",
                    "-f", "docx",
                    "-t", "markdown",
                    "--wrap=none",  // 不自动换行
                    inputPath,
                    "-o", outputPath
            );
        }

        // 执行命令
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("pandoc 命令执行失败，退出码: " + exitCode);
        }
    }

    /**
     * 将 docx 文件转换为 markdown，并返回转换后的内容字符串
     * @param inputPath 输入的 docx 文件路径
     * @return 转换后的 markdown 内容
     * @throws IOException 如果文件操作失败或 pandoc 命令执行失败
     * @throws InterruptedException 如果命令执行被中断
     */
//    public static String convertDocxToMarkdownString(String inputPath) throws IOException, InterruptedException {
//        return convertDocxToMarkdownString(inputPath, null); // 默认不处理图片
//    }

    /**
     * 将 docx 文件转换为 markdown，并返回转换后的内容字符串，同时可提取图片
     * @param inputPath 输入的 docx 文件路径
     * @param imageOutputDir 图片输出目录，如果为 null 则不提取图片
     * @return 转换后的 markdown 内容
     * @throws IOException 如果文件操作失败或 pandoc 命令执行失败
     * @throws InterruptedException 如果命令执行被中断
     */
    public static List<String> convertDocxToMarkdownString(String inputPath, String imageOutputDir) throws IOException, InterruptedException {
        // 验证输入文件是否存在
        Path input = Paths.get(inputPath);
        if (!Files.exists(input)) {
            throw new IOException("输入文件不存在: " + inputPath);
        }

        if (!inputPath.toLowerCase().endsWith(".docx")) {
            throw new IllegalArgumentException("输入文件必须是 .docx 格式");
        }

        // 创建临时文件存储转换结果
        Path tempOutput = Files.createTempFile("temp_md_", ".md");

        try {
            // 使用 pandoc 进行转换，支持图片提取
            ProcessBuilder processBuilder;
            if (imageOutputDir != null && !imageOutputDir.trim().isEmpty()) {
                // 创建图片输出目录
                Path imageDir = Paths.get(imageOutputDir);
                Files.createDirectories(imageDir);

                processBuilder = new ProcessBuilder(
                        "pandoc",
                        "-f", "docx",
                        "-t", "markdown",
                        "--wrap=none",
                        "--extract-media=" + imageOutputDir,  // 提取媒体文件到指定目录
                        inputPath,
                        "-o", tempOutput.toString()
                );
            } else {
                processBuilder = new ProcessBuilder(
                        "pandoc",
                        "-f", "docx",
                        "-t", "markdown",
                        "--wrap=none",
                        inputPath,
                        "-o", tempOutput.toString()
                );
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("pandoc 命令执行失败，退出码: " + exitCode);
            }

            // 读取转换后的 markdown 内容
            String file = Files.readString(tempOutput);
            return processContext(file);
        } finally {
            // 删除临时文件
            try {
                Files.deleteIfExists(tempOutput);
            } catch (IOException e) {
                System.err.println("无法删除临时文件: " + tempOutput.toString());
            }
        }
    }

    /**
     * 检查系统是否安装了 pandoc
     * @return 如果 pandoc 可用则返回 true，否则返回 false
     */
    public static boolean isPandocAvailable() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("pandoc", "--version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * 处理转换后的内容，
     * @param context 转换后的内容
     * @return 处理后的内容
     */
    private static List<String> processContext(String context) {
        if (context.isEmpty()) {
            return null;
        }
        context = context.replace("$$", "$").replaceAll("!\\[([^\\]]*)\\]\\(([^)]+)\\)", "<img src=\"$2\" alt=\"$1\" width=\"200\" style=\"display:block;margin:auto;float:right;\" />");
        String regex = "\\[.*?\\]\\{\\.underline\\}";
        String regex1 = "\\{\\s*width\\s*=\\s*\"[^\"]*\"[^}]*?\\}|\\{\\s*[^}]*?width\\s*=\\s*\"[^\"]*\"[^}]*?\\}";
        context = Pattern.compile(regex1).matcher(context).replaceAll("");
        String newContext = Pattern.compile(regex).matcher(context).replaceAll("(&emsp;&emsp;)");

        ArrayList<String> strings = new ArrayList<>();
        List<String> list = Arrays.stream(newContext.split("===========")).toList();

        for (int i = 0; i < list.size()-1; i++) {
            int index = (i / 2) + 1;
            if (i % 2 == 1 && !Objects.equals(list.get(i), "")) {
                strings.add("<div>【举一反三" + index + "】</div>" + list.get(i) + "<br><br><br><br><br><br>");
            } else {
                strings.add("<div>【温故知新" + index + "】</div>" + list.get(i) + "<br><br><br><br><br><br>");
            }
        }
        return strings;
    }

    /**
     * 将内容写入 markdown 文件
     * @param content 要写入的内容
     * @param filePath 文件路径
     * @throws IOException 如果文件操作失败
     */
    public void writeToMarkdown(String content, String filePath) throws IOException {
        Files.createDirectories(Paths.get(filePath).getParent());

        // 使用 StandardOpenOption.APPEND 以追加模式写入
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND);
    }


    public void markdownToHtml(String inputMd, String outputHtml) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("pandoc", inputMd, "-o", outputHtml);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Pandoc markdown to html conversion failed with exit code: " + exitCode);
        }
    }

//    public void htmlToPdf(String inputHtml, String outputPdf) throws Exception {
//        // 读取 HTML 文件
//        String htmlContent = Files.readString(Paths.get(inputHtml));
//
//        // 使用 Flying Saucer 将 HTML 转换为 PDF
//        try (OutputStream os = new FileOutputStream(outputPdf)) {
//            // 使用 Flying Saucer 的 ITextRenderer
//            org.xhtmlrenderer.pdf.ITextRenderer renderer = new org.xhtmlrenderer.pdf.ITextRenderer();
//            renderer.setDocumentFromString(htmlContent);
//            renderer.layout();
//            renderer.createPDF(os); // 直接使用输出流
//        }
//    }
//
//
//
//
//    public void markdownToPdf(String inputMd, String outputPdf) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder(
//                "pandoc",
//                inputMd,
//                "-o",
//                outputPdf,
//                "--pdf-engine=xelatex",  // 指定支持中文的引擎
//                "-V", "CJKmainfont=SimSun"  // 设置中文字体
//        );
//        Process process = pb.start();
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            throw new IOException("Pandoc conversion failed with exit code: " + exitCode);
//        }
//    }
}
