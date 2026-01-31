package com.keinn.grade_analysis_v2.controller;

import com.keinn.grade_analysis_v2.utils.PandocUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class testController {

    @RequestMapping("/test")
    public String test(){
        PandocUtils pandocUtils = new PandocUtils();
        if (!PandocUtils.isPandocAvailable()) {
            System.out.println("未安装pandoc");
        }
        ArrayList<HashMap<String, List<String>>> stuInfos = new ArrayList<>();
        HashMap<String, List<String>> stuInfo = new HashMap<>();
        stuInfo.put("张三", Arrays.asList("1", "2", "4"));
        stuInfo.put("李四", Arrays.asList("2", "3", "7"));

        pandocUtils.processQuestions(stuInfo, "testPandoc");
        return "test";
    }
}
