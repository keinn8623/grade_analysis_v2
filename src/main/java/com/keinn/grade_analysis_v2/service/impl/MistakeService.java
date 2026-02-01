package com.keinn.grade_analysis_v2.service.impl;

import com.keinn.grade_analysis_v2.common.AssignValuesToObject;
import com.keinn.grade_analysis_v2.model.GradeFromExcel;
import com.keinn.grade_analysis_v2.model.StuTextInfo;
import com.keinn.grade_analysis_v2.service.IMistakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public List<StuTextInfo> explainExcelFile(String excelFilePath) throws IOException {
        List<List<String>> result = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 只读取第一个 sheet
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
//        result.forEach(System.out::println);
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

    private void saveObjectGrade(List<List<String>> result) {
        // 题型
        List<String> questionTypes = result.get(1);
        if (!CollectionUtils.isEmpty(questionTypes)) {
            questionTypes.removeIf(type -> type.equals("NaN"));
        }
        System.out.println(questionTypes);

        List<GradeFromExcel> gradeFromExcels = new ArrayList<>();
        for (int i = 2; i < result.size(); i++) {
            GradeFromExcel obj = assignValuesToObject.assignValues(result.get(i), GradeFromExcel.class);
            gradeFromExcels.add(obj);
        }

        // 保存
//        saveData(gradeFromExcels);

    }

    private void analyzeData(List<List<String>> data) {
        // 题型
        List<String> questionTypes = data.get(0);
        if (!CollectionUtils.isEmpty(questionTypes)) {
            questionTypes.removeIf(type -> type.equals("NaN"));
        }
        List<Object> result = new ArrayList<>();
        List<Map<List<String>,Map<String, Integer>>> gradeNTypeLists = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            Map<List<String>,Map<String, Integer>> gradeNTypes = new HashMap<>();
            Map<String, Integer> gradeNType = new HashMap<>();
            List<String> infos = data.get(i).subList(0, 3);
            List<String> newRow = data.get(i).subList(3, data.get(i).size());
            newRow.removeIf(String::isBlank);
            System.out.println(newRow);
            if (newRow.size() != questionTypes.size()) {
                return;
            }
            gradeNType = combineListsToMap(questionTypes, newRow);
            gradeNTypes.put(infos, gradeNType);
            gradeNTypeLists.add(gradeNTypes);
        }
        // {[满分, 文航, 大坪]={数论=6, 计算=18, 组合=6, 计数=6, 应用=9, 几何=9, 行程=6}}
        // TODO 修改数据格式&Excel模板
        gradeNTypeLists.forEach(System.out::println);
    }

    private Map<String, Integer> combineListsToMap(List<String> keys, List<String> values) {
        Map<String, Integer> resultMap = new HashMap<>();

        for (int i = 0; i < Math.min(keys.size(), values.size()); i++) {
            String key = keys.get(i);
            Integer value = Integer.valueOf(values.get(i));

            resultMap.merge(key, value, Integer::sum);
        }

        return resultMap;
    }
}
