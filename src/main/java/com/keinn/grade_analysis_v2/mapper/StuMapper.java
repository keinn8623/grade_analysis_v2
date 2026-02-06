package com.keinn.grade_analysis_v2.mapper;

import com.keinn.grade_analysis_v2.model.QueryStuInfo;
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
    List<StuInfo> getStuInfos(QueryStuInfo queryObj);

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

    /**
     * 删除学生信息
     *
     * @param ids 学生id列表
     * @return 删除结果
     */
    boolean deleteStuInfoById(List<Long> ids);

    /**
     * 修改学生信息
     *
     * @param stuInfo 学生信息
     * @return 修改结果
     */
    boolean updateStuInfo(StuInfo stuInfo);
}
