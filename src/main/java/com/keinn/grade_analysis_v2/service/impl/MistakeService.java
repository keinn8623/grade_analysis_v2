package com.keinn.grade_analysis_v2.service.impl;

import com.keinn.grade_analysis_v2.common.AssignValuesToObject;
import com.keinn.grade_analysis_v2.common.Enum.OverAllData;
import com.keinn.grade_analysis_v2.model.GradeFromExcel;
import com.keinn.grade_analysis_v2.model.StuTextInfo;
import com.keinn.grade_analysis_v2.model.StuTextOverAllInfo;
import com.keinn.grade_analysis_v2.service.IMistakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MistakeService implements IMistakeService {
    private static final AssignValuesToObject assignValuesToObject = new AssignValuesToObject();


    /**
     * 解析Excel文件
     *
     * @param excelFilePath
     * @return
     */
    @Override
    public List<StuTextInfo> explainExcelFile(String excelFilePath) throws Exception {
        List<List<String>> result = new ArrayList<>();
        String sheetName;
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 只读取第一个 sheet
            sheetName = workbook.getSheetAt(0).getSheetName();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                List<String> rowData = new ArrayList<>();

                if (row != null) {
                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex);
                        rowData.add(getCellValue(cell));
                    }
                }
                result.add(rowData);
            }
        } catch (IOException e) {
            log.error("Error reading Excel file: {}", e.getMessage());
            return new ArrayList<>();
        }
//        saveObjectGrade(result);
        analyzeData(result);
        boolean isSuccess = overAllData(result);
//
        return List.of();
    }

    /**
     * 获取单元格的值
     *
     * @param cell cell
     * @return 单元格的值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        // 处理不同的数据类型
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                // 判断是否为日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 普通数字，防止科学计数法或长数字变小数
                    // 如果是整数，返回整数形式
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.valueOf((long) value);
                    } else {
                        return String.valueOf(value);
                    }
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                // 如果是公式，获取计算后的值
                return String.valueOf(cell.getCellFormula());
    //            return getCellValue(cell.getCellFormula());

            case BLANK:
                return "";

            default:
                return "未知类型";
        }
    }

    private void analyzeData(List<List<String>> data) throws Exception {
        System.out.printf("data:%s", data);

        String textName = data.get(1).get(0); // 考试名称
        List<String> standardScore = data.get(1).subList(3, data.get(1).size());  // 试卷满分
        List<StuTextInfo> stuTextInfos = new ArrayList<>();

        for (int i = 2; i < data.size(); i++) {
            StuTextInfo stuTextInfo = new StuTextInfo();
            String stuName = data.get(i).get(0);  // 学生姓名
            String teaName = data.get(i).get(1);    // 老师姓名
            String campus = data.get(i).get(2); // 校区

            List<String> stuScores = data.get(i).subList(3, data.get(i).size());  // 学生各题得分情况

            if (standardScore.size() != stuScores.size()) {
                log.warn("{}：学生得分数据不全", LocalDateTime.now());
                throw new Exception("学生得分数据不全,请重新导入文件");
            }
            List<Integer> pointsLost = new ArrayList<>();
            List<Integer> scores = new ArrayList<>();

            for (int j = 0; j < standardScore.size(); j++) {
                if (Integer.parseInt(stuScores.get(j)) == 0 || ((double) Integer.parseInt(stuScores.get(j)) / Integer.parseInt(standardScore.get(j)) < 0.6)) {
                    pointsLost.add(j+1);
                } else {
                    scores.add(j+1);
                }
            }
            stuTextInfo.setStuTextInfoId(System.currentTimeMillis());
            stuTextInfo.setStuName(stuName);
            stuTextInfo.setTextName(textName);
            stuTextInfo.setTeaName(teaName);
            stuTextInfo.setCampus(campus);
            stuTextInfo.setPointsGetNo(scores);
            stuTextInfo.setPointsLostNo(pointsLost);
            stuTextInfos.add(stuTextInfo);
        }
        System.out.println();
        stuTextInfos.forEach(System.out::println);
    }

    private boolean overAllData(List<List<String>> data) throws Exception {
        String textName = data.get(1).get(0); // 考试名称

        // 获取标题行作为键
        List<String> infoHeaders = data.get(0).subList(0, 3);
        List<String> scoreHeaders = data.get(0).subList(3, data.get(0).size());
        if (CollectionUtils.isEmpty(infoHeaders) || CollectionUtils.isEmpty(scoreHeaders)) {
            return false;
        }

        // 创建每行数据对应的map列表，使用LinkedHashMap保持插入顺序
        List<Map<String, String>> resultMaps = new ArrayList<>();

        // 从第二行开始处理每一行数据
        for (int i = 1; i < data.size(); i++) {
            List<String> infos = data.get(i).subList(0,3);
            List<String> currentRow = data.get(i).subList(3, data.get(i).size());

            Map<String, String> infoMap = new LinkedHashMap<>();
            Map<String, String> scoreMap = new LinkedHashMap<>(); // 使用LinkedHashMap保持顺序

            // infoHeader
            for (int j = 0; j < Math.min(infoHeaders.size(), infos.size()); j++) {
                String infoKey = infoHeaders.get(j);
                String infoValue = infos.get(j);
                infoMap.put(infoKey, infoValue);
                infoMap.put(OverAllData.TEXT_NAME.getName(), textName);
            }

            // 将标题与当前行的值配对，按照scoreHeader的顺序
            for (int j = 0; j < Math.min(scoreHeaders.size(), currentRow.size()); j++) {
                String scoreKey = scoreHeaders.get(j);
                String scoreValue = currentRow.get(j);

                // 尝试将值转换为整数
                try {
                    int intValue = Integer.parseInt(scoreValue);

                    // 检查是否已存在该键
                    if (scoreMap.containsKey(scoreKey)) {
                        // 获取现有值并转换为整数进行相加
                        int existingValue = Integer.parseInt(scoreMap.get(scoreKey));
                        int sum = existingValue + intValue;
                        scoreMap.put(scoreKey, String.valueOf(sum));
                    } else {
                        // 键不存在，直接添加
                        scoreMap.put(scoreKey, scoreValue);
                    }
                } catch (NumberFormatException e) {
                    // 如果不能转换为整数，跳过此值
                    log.warn("无法将 '{}' 转换为整数，跳过该值", scoreValue);
                    throw new Exception("分数:"+scoreValue+"错误");
                    // 如果已有该键，则保留原值；如果没有该键，也不添加
                }
            }
            infoMap.putAll(scoreMap);
            resultMaps.add(infoMap);
        }

        System.out.println(textName);
        List<StuTextOverAllInfo> allInfos = new ArrayList<>();
        // 输出结果验证
        resultMaps.forEach(System.out::println);
        resultMaps.forEach(r -> {
            StuTextOverAllInfo allInfo = new StuTextOverAllInfo();
            allInfo.setStuTextOverAllInfoId(System.currentTimeMillis());
            allInfo.setStuName(Optional.of(r.get(OverAllData.STU_NAME.getName())).orElse(""));
            allInfo.setTextName(Optional.of(r.get(OverAllData.TEXT_NAME.getName())).orElse(""));
            allInfo.setTeaName(Optional.of(r.get(OverAllData.TEA_NAME.getName())).orElse(""));
            allInfo.setCampus(Optional.of(r.get(OverAllData.CAMPUS.getName())).orElse(""));
            allInfo.setNumTheory(Integer.parseInt(Optional.of(r.get(OverAllData.NUM_THEORY.getName())).orElse("0")));
            allInfo.setJourney(Integer.parseInt(Optional.of(r.get(OverAllData.JOURNEY.getName())).orElse("0")));
            allInfo.setApplication(Integer.parseInt(Optional.of(r.get(OverAllData.APPLICATION.getName())).orElse("0")));
            allInfo.setCombination(Integer.parseInt(Optional.of(r.get(OverAllData.COMBINATION.getName())).orElse("0")));
            allInfo.setGeometry(Integer.parseInt(Optional.of(r.get(OverAllData.GEOMETRY.getName())).orElse("0")));
            allInfo.setCounter(Integer.parseInt(Optional.of(r.get(OverAllData.COUNTER.getName())).orElse("0")));
            allInfo.setCalculation(Integer.parseInt(Optional.of(r.get(OverAllData.CALCULATION.getName())).orElse("0")));
            allInfos.add(allInfo);
        });
        System.out.println("保存对象");
        allInfos.forEach(System.out::println);
        // 保存结果
//        save();
        return true;
    }
}
