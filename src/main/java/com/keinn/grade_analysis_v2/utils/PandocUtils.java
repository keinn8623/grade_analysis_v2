package com.keinn.grade_analysis_v2.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class PandocUtils {

    private static final String ALL = "all";
    private static final String SINGLE = "single";
    private static final String QUESTION = "question";
    private static final String RESOLUTION = "resolution";

    /**
     * 将 docx 文件转换为 markdown，并返回转换后的内容字符串，同时可提取图片
     * @param inputPath 输入的 docx 文件路径
     * @param imageOutputDir 图片输出目录，如果为 null 则不提取图片
     * @return 转换后的 markdown 内容
     * @throws IOException 如果文件操作失败或 pandoc 命令执行失败
     * @throws InterruptedException 如果命令执行被中断
     */
    public List<String> convertDocxToMarkdownString(String inputPath, String imageOutputDir, String processWay, String version) throws IOException, InterruptedException {
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
            System.out.printf("file:%s", file);
            if (processWay.equals(ALL)) {
                return originalAndDrawInferences(file, version);
            } else {
                return drawInferences(file, version);
            }

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
     * 包含温故知新和举一反三题型
     * @param context 转换后的内容
     * @return 处理后的内容
     */
    private List<String> originalAndDrawInferences(String context, String version) {
        ArrayList<String> strings = new ArrayList<>();
        List<String> list = processRegContext(context);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        for (int i = 0; i < list.size()-1; i++) {
            int index = (i / 2) + 1;
            String stringContext = list.get(i);
            if (i % 2 == 1 && !Objects.equals(stringContext, "")) {
                if (version.equals(QUESTION)) {
                    if (Pattern.compile("!\\[]\\(.*?\\)").matcher(stringContext).find()) {
                        strings.add("【举一反三" + index + "】\n" + stringContext + "\n\n\n\n\n\n");
                    } else {
                        strings.add("【举一反三" + index + "】\n" + stringContext + "![](resource/template.png)"+ "\n\n\n\n\n\n");
                    }
                } else if (version.equals(RESOLUTION)) {
                    if (Pattern.compile("!\\[]\\(.*?\\)").matcher(stringContext).find()) {
                        strings.add("【举一反三" + index + "】\n" + stringContext);
                    } else {
                        strings.add("【举一反三" + index + "】\n" + stringContext);
                    }
                }
            } else {
                if (Pattern.compile("!\\[]\\(.*?\\)").matcher(stringContext).find()) {
                    strings.add("【温故知新" + index + "】\n" + stringContext + "\n\n\n\n\n\n");
                } else {
                    strings.add("【温故知新" + index + "】\n" + stringContext + "![](resource/template.png)"+ "\n\n\n\n\n\n");
                }
            }

        }
        return strings;
    }

    /**
     * 处理只有举一反三题目
     * @param context 转换后的内容
     * @return 处理后的内容只有举一反三版本
     */
    private List<String> drawInferences(String context, String version) {
        ArrayList<String> strings = new ArrayList<>();
        List<String> list = processRegContext(context);

        for (int i = 0; i < list.size()-1; i++) {
            int index = (i / 2) + 1;
            String stringContext = list.get(i);
            if (i % 2 == 1 && !Objects.equals(stringContext, "")) {
                if (Pattern.compile("!\\[]\\(.*?\\)").matcher(stringContext).find()) {
                    strings.add("【举一反三" + index + "】\n" + stringContext + "\n\n\n\n\n\n");
                } else {
                    strings.add("【举一反三" + index + "】\n" + stringContext + "![](resource/template.png)"+ "\n\n\n\n\n\n");
                }

            }
        }
        return strings;
    }

    /**
     * 正则处理文件的内容，
     * @param context 转换后的内容
     * @return 正则处理后的内容
     */
    private List<String> processRegContext(String context) {
        if (context.isEmpty()) {
            return null;
        }
        context = context.replace("$$", "$");
        String regex = "\\[.*?\\]\\{\\.underline\\}";
        String regex1 = "\\{\\s*width\\s*=\\s*\"[^\"]*\"[^}]*?\\}|\\{\\s*[^}]*?width\\s*=\\s*\"[^\"]*\"[^}]*?\\}";
        context = Pattern.compile(regex1).matcher(context).replaceAll("");
        String newContext = Pattern.compile(regex).matcher(context).replaceAll("(&emsp;&emsp;)");
        return Arrays.stream(newContext.split("===========")).toList();
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


    /**
     * 将 markdown 文件转换为 pdf 文件
     * @param inputFilePath markdown 文件路径
     * @param outputFilePath pdf 文件路径
     * @throws Exception 如果转换失败
     */
    public void convertPDF(String inputFilePath, String outputFilePath, String name) {
        // 验证输入文件是否存在
        Path inputPath = Paths.get(inputFilePath);
        if (!Files.exists(inputPath)) {
            System.err.println("输入文件不存在: " + inputFilePath);
            return;
        }
        // 确保输出目录存在
        try {
            Files.createDirectories(Paths.get(outputFilePath).getParent());
        } catch (IOException e) {
            System.err.println("无法创建输出目录: " + e.getMessage());
            return;
        }

        String[] command = {
                "pandoc",
                inputFilePath,
                "-o", outputFilePath + "/" + name + ".pdf",
                "--pdf-engine=xelatex",
                "--from=markdown+tex_math_dollars+raw_tex",
                "--variable=mainfont=Times New Roman",
                "--variable=CJKmainfont=SimSun",  // 设置中文字体
                "--variable=geometry:margin=1in",
                "--variable=documentclass=article"
        };

        try {
            executeCommand(command);
        } catch (Exception e) {
            log.error("转换失败: {}", e.getMessage());
        }
    }

    /**
     * 处理生成信息
     * @param stuInfo 学生信息
     * @param dirPath 文件路径
     */
    public void processQuestions(HashMap<String, List<String>> stuInfo, String dirPath, String processWay, String version) {
        if (stuInfo == null) {
            return;
        }
        String currentTime = String.valueOf(System.currentTimeMillis());

        stuInfo.forEach((name, questions) -> {
            try {
                List<String> s = convertDocxToMarkdownString(dirPath + "/word.docx","testPandoc", processWay, version);

                if (questions ==  null) {
                    return;
                }
                for (String index : questions) {
                    String f = "【温故知新" + index + "】\n";
                    String e = "【举一反三" + index + "】\n";
                    s.forEach(item -> {
                        try {
                            if (item.contains(e) || item.contains(f))
                                writeToMarkdown(item, dirPath+ "/template.md");
                        } catch (IOException exp) {
                            throw new RuntimeException(exp);
                        }
                    });
                }
                // TODO redis 一个小时缓存
                convertPDF(dirPath+ "/template.md", "testPandoc/pdf/"+ currentTime, name);
            } catch (Exception  e) {

            } finally {
                Path template = Paths.get(dirPath + "/template.md");
                try {
                    // 删除临时文件
                    Files.deleteIfExists(template);
                } catch (IOException e) {
                    System.err.println("无法删除临时文件: " + template.getFileName());
                }
            }
        });
    }
    private static void executeCommand(String[] command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);  // 合并标准输出和错误输出

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {  // 明确指定编码
            String line;
            StringBuilder output = new StringBuilder();  // 收集输出信息
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("命令执行失败，输出信息：" + output.toString());
                throw new RuntimeException("命令执行失败，退出码: " + exitCode + ", 命令: " + String.join(" ", command));
            }
        }
    }












}
