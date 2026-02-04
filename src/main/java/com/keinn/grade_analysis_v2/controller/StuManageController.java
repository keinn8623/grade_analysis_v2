package com.keinn.grade_analysis_v2.controller;


import com.keinn.grade_analysis_v2.common.ResponseResult;
import com.keinn.grade_analysis_v2.model.StuInfo;
import com.keinn.grade_analysis_v2.service.IStuManage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@Slf4j
@RestController
@RequestMapping("/stu")
@RequiredArgsConstructor
public class StuManageController {

    private final IStuManage stuManage;

    @RequestMapping("/add")
    public ResponseResult<StuInfo> addStu(@Param("formValues") StuInfo stuInfo){
        stuInfo.setCreateUser("张三");

        System.out.println(stuInfo);
        boolean isSuccess = false;
        try {
            isSuccess = stuManage.addStudent(stuInfo);
        } catch (Exception e) {
            log.warn("添加学生失败: {}", e.getMessage());
            return ResponseResult.fail(e.getMessage());
        }
        if (!isSuccess) {
            return ResponseResult.fail("添加失败");
        }
        return ResponseResult.success("添加成功", stuInfo);
    }
}
