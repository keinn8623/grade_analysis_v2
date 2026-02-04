package com.keinn.grade_analysis_v2.controller;

import com.keinn.grade_analysis_v2.model.StuInfo;
import com.keinn.grade_analysis_v2.service.IMistakeService;
import com.keinn.grade_analysis_v2.service.IStuManage;
import com.keinn.grade_analysis_v2.service.impl.MistakeService;
import com.keinn.grade_analysis_v2.service.impl.StuManage;
import com.keinn.grade_analysis_v2.utils.PandocUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class testController {

    private final IMistakeService mistakeService;
    private final IStuManage stuManage;


    @GetMapping("/test")
    public String test(@Param("stuInfo") HashMap<String, List<String>> stuInfoT) {
        PandocUtils pandocUtils = new PandocUtils();
        if (!PandocUtils.isPandocAvailable()) {
            System.out.println("未安装pandoc");
        }
        ArrayList<HashMap<String, List<String>>> stuInfos = new ArrayList<>();
        HashMap<String, List<String>> stuInfo = new HashMap<>();
        stuInfo.put("张三", Arrays.asList("1", "2", "4"));
        stuInfo.put("李四", Arrays.asList("2", "3", "7"));

        try {
            Path path = Paths.get("testPandoc");
            Files.walk(path)
                    .filter(p -> !p.equals(path))
                    .filter(Files::isRegularFile)
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            log.error("无法删除文件: {}", e.getMessage());
                        }
                    });
        } catch (IOException e) {
            log.error("删除文件夹: {}", e.getMessage());
        }
        pandocUtils.processQuestions(stuInfo, "resource", "all", "question");
//        pandocUtils.processQuestions(stuInfo, "testPandoc", "single");
        return "test";
    }

    @GetMapping("/readExcel")
    public String readExcel() throws Exception {
        String excelFile = "./resource/excel.xlsx";
        mistakeService.explainExcelFile(excelFile);
        return "test";
    }

}
