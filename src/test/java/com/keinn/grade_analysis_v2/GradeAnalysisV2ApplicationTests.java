package com.keinn.grade_analysis_v2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

@SpringBootTest
class GradeAnalysisV2ApplicationTests {

    @Test
    void contextLoads() {
        // 测试字符串（[]里可以是任意内容：空格、文字、甚至空的）
        String text1 = "这是测试[          ]{.underline}结束";
        String text2 = "这是测试[这里是文字]{.underline}结束";
        String text3 = "这是测试[]{.underline}结束";

        // 正则表达式
        String regex = "\\[.*?\\]\\{\\.underline\\}";

        // 检查是否包含该模式
        boolean match1 = Pattern.compile(regex).matcher(text1).find();
        boolean match2 = Pattern.compile(regex).matcher(text2).find();
        boolean match3 = Pattern.compile(regex).matcher(text3).find();

        System.out.println("text1 匹配: " + match1); // true
        System.out.println("text2 匹配: " + match2); // true
        System.out.println("text3 匹配: " + match3); // true
    }

}
