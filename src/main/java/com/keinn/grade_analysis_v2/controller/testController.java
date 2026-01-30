package com.keinn.grade_analysis_v2.controller;

import com.keinn.grade_analysis_v2.utils.PandocUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class testController {

    @RequestMapping("/test")
    public String test(){
        PandocUtils pandocUtils = new PandocUtils();
        if (!PandocUtils.isPandocAvailable()) {
            System.out.println("未安装pandoc");
        }
        ArrayList<String> integers = new ArrayList<>();
        integers.add("2");
        integers.add("5");
        integers.add("7");
        try {
            List<String> s = pandocUtils.convertDocxToMarkdownString("testPandoc/word.docx","testPandoc/");
            s.forEach(System.out::println);
            for (String index : integers) {
                String f = "【温故知新" + index + "】";
                String e = "【举一反三" + index + "】";
                s.forEach(item -> {
                    try {
                        if (item.contains(e) || item.contains(f))
                        pandocUtils.writeToMarkdown(item, "testPandoc/word.md");
                    } catch (IOException exp) {
                        throw new RuntimeException(exp);
                    }
                });
            }
//            pandocUtils.markdownToHtml("testPandoc/word.md", "testPandoc/word.html");
//            pandocUtils.htmlToPdf("testPandoc/word.html", "testPandoc/word.pdf");
            } catch (Exception  e) {
                e.printStackTrace();
        }


        return "test";
    }
}
