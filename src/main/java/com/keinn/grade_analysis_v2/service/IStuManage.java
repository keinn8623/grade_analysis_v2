package com.keinn.grade_analysis_v2.service;

import com.keinn.grade_analysis_v2.model.StuInfo;

import java.util.List;

public interface IStuManage {



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
    List<StuInfo> queryStudents(StuInfo stuInfo);

    /**
     * 修改学生信息
     *
     * @param stuInfo 学生对象
     * @return true or false
     */
    boolean updateStudent(StuInfo stuInfo);

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
     * @param stuInfoIds 学生对象列表
     * @return true or false
     */
    boolean batchDeleteStudent(List<String> stuInfoIds);
}
