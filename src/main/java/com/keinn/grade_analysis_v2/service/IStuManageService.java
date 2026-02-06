package com.keinn.grade_analysis_v2.service;

import com.keinn.grade_analysis_v2.model.QueryStuInfo;
import com.keinn.grade_analysis_v2.model.StuInfo;

import java.util.List;

public interface IStuManageService {



    /**
     * 添加学生
     *
     * @param stuInfo 学生对象
     * @return true or false
     */
    boolean addStudent(StuInfo stuInfo) throws Exception;

    /**
     * 批量添加学生
     *
     * @param stuInfoList 学生对象列表
     * @return true or false
     */
    boolean batchAddStudent(List<StuInfo> stuInfoList);

    /**
     * 查询学生
     *
     * @param stuInfo 学生对象
     * @return 查询结果
     */
    List<StuInfo> queryStudents(QueryStuInfo stuInfo) throws Exception;

    /**
     * 修改学生信息
     *
     * @param stuInfo 学生对象
     * @return true or false
     */
    boolean updateStudent(StuInfo stuInfo) throws Exception;

    /**
     * 删除学生
     *
     * @param stuInfo 学生对象
     * @return true or false
     */
    boolean deleteStudent(StuInfo stuInfo);

    /**
     * 批量删除学生
     *
     * @param stuInfoIds 学生对象id列表
     * @return true or false
     */
    boolean batchDeleteStudent(List<Long> stuInfoIds);


}
