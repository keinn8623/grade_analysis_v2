package com.keinn.grade_analysis_v2.mapper;

import com.keinn.grade_analysis_v2.model.StuInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StuMapper {
    /**
     * 添加学生信息
     *
     * @param stuInfo 学生信息
     * @return 添加结果
     */
    int addStuInfo(StuInfo stuInfo);

    /**
     * 查询学生信息
     *
     * @param queryObj 查询条件
     * @return 学生列表
     */
    List<StuInfo> getStuInfos(StuInfo queryObj);

    /**
     * 根据id查询学生信息
     *
     * @param id 学生id
     * @return 学生信息
     */
    StuInfo getStuInfoById(Long id);

    /**
     * 根据姓名和手机号查询学生信息
     *
     * @param stuName 姓名
     * @param phone 手机号
     * @return 学生信息
     */
    StuInfo getStuInfoByStuNameAndPhone(String stuName, String phone);
}
