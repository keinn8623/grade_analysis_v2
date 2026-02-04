package com.keinn.grade_analysis_v2.service;

import com.keinn.grade_analysis_v2.model.StuTextInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public interface IMistakeService {

    /**
     * 解析Excel文件
     * @param excelFilePath
     * @return
     */
    List<StuTextInfo> explainExcelFile(String excelFilePath) throws Exception;
}
