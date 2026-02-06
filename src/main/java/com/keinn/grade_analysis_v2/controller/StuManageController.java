package com.keinn.grade_analysis_v2.controller;


import com.keinn.grade_analysis_v2.common.ResponseResult;
import com.keinn.grade_analysis_v2.model.QueryStuInfo;
import com.keinn.grade_analysis_v2.model.StuInfo;
import com.keinn.grade_analysis_v2.service.IStuManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stu")
@RequiredArgsConstructor
public class StuManageController {

    private final IStuManageService stuManage;

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

    @RequestMapping("/query")
    public ResponseResult<List<StuInfo>> queryStuInfos(@Param("queryObj") QueryStuInfo queryObj){
        List<StuInfo> stuInfos = new ArrayList<>();
        try {
            stuInfos = stuManage.queryStudents(queryObj);
            if (stuInfos.isEmpty()) {
                return ResponseResult.success("没有查询到数据", stuInfos);
            }
        } catch (Exception e) {
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.success("查询成功", stuInfos);
    }

    @RequestMapping("/delete")
    public ResponseResult<Long> deleteStudents(@RequestBody List<Long> ids){
        boolean isSuccess;
        try {
            isSuccess = stuManage.batchDeleteStudent(ids);
        } catch (Exception e) {
            log.warn("删除学生失败: {}", e.getMessage());
            return ResponseResult.fail(e.getMessage());
        }
        if (!isSuccess) {
            return ResponseResult.fail("删除失败");
        }
        return ResponseResult.success("删除成功");
    }

    @RequestMapping("/update")
    public ResponseResult<?> updateStudent(@RequestBody StuInfo stuInfo){
        System.out.println("修改==================");
        System.out.println(stuInfo);
        boolean isSuccess;
        try {
            isSuccess = stuManage.updateStudent(stuInfo);
        } catch (Exception e) {
            log.warn("更新学生失败: {}", e.getMessage());
            return ResponseResult.fail(e.getMessage());
        }
        if (!isSuccess) {
            return ResponseResult.fail("更新失败");
        }
        return ResponseResult.success("更新成功");
    }
}
