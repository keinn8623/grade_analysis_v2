package com.keinn.grade_analysis_v2.service.impl;

import com.keinn.grade_analysis_v2.mapper.StuMapper;
import com.keinn.grade_analysis_v2.model.StuInfo;
import com.keinn.grade_analysis_v2.service.IStuManage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StuManage implements IStuManage {


    private final StuMapper stuMapper;


    /**
     * 查询学生
     *
     * @param stuInfo 查询条件
     * @return 学生列表
     */
    @Override
    public List<StuInfo> queryStudents(StuInfo stuInfo) {
        return CollectionUtils.isEmpty(stuMapper.getStuInfos(stuInfo))?
                new ArrayList<>() : stuMapper.getStuInfos(stuInfo);
    }

    /**
     * 添加学生
     *
     * @param stuInfo 学生对象
     * @return true or false
     */
    @Override
    public boolean addStudent(StuInfo stuInfo) throws Exception {
        StuInfo isExist = stuMapper.getStuInfoByStuNameAndPhone(stuInfo.getStuName(), stuInfo.getPhone());
        if (!Objects.isNull(isExist)) {
            throw new Exception("该学生已存在");
        }
        return stuMapper.addStuInfo(stuInfo) > 0;
    }

    /**
     * 批量添加学生
     *
     * @param stuInfoList 学生对象列表
     * @return true or false
     */
    @Override
    public boolean batchAddStudent(List<StuInfo> stuInfoList) {
        return false;
    }

    /**
     * 修改学生信息
     *
     * @param stuInfo 学生对象
     * @return true or false
     */
    @Override
    public boolean updateStudent(StuInfo stuInfo) {
        return false;
    }

    /**
     * 删除学生
     *
     * @param stuInfo 学生对象
     * @return true or false
     */
    @Override
    public boolean deleteStudent(StuInfo stuInfo) {
        return false;
    }

    /**
     * 批量删除学生
     *
     * @param stuInfoIds 学生对象列表
     * @return true or false
     */
    @Override
    public boolean batchDeleteStudent(List<String> stuInfoIds) {
        return false;
    }
}
