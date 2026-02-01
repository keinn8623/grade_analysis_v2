package com.keinn.grade_analysis_v2.controller;

import com.keinn.grade_analysis_v2.service.IMistakeService;
import com.keinn.grade_analysis_v2.service.impl.MistakeService;
import com.keinn.grade_analysis_v2.utils.PandocUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class testController {

    private static final IMistakeService mistakeService = new MistakeService();

    @RequestMapping("/test")
    public String test(@Param("stuInfo") HashMap<String, List<String>> stuInfoT){
        PandocUtils pandocUtils = new PandocUtils();
        if (!PandocUtils.isPandocAvailable()) {
            System.out.println("未安装pandoc");
        }
        ArrayList<HashMap<String, List<String>>> stuInfos = new ArrayList<>();
        HashMap<String, List<String>> stuInfo = new HashMap<>();
        stuInfo.put("张三", Arrays.asList("1", "2", "4"));
        stuInfo.put("李四", Arrays.asList("2", "3", "7"));

        pandocUtils.processQuestions(stuInfo, "testPandoc", "all", "question");
//        pandocUtils.processQuestions(stuInfo, "testPandoc", "single");
        return "test";
    }

    @RequestMapping("/readExcel")
    public String readExcel() throws IOException {
        String excelFile = "./testPandoc/excel.xlsx";
        mistakeService.explainExcelFile(excelFile);
        return "test";
    }
}
