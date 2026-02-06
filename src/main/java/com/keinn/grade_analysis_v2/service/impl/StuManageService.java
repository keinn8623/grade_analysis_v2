package com.keinn.grade_analysis_v2.service.impl;

import com.keinn.grade_analysis_v2.common.DateFormatConverter;
import com.keinn.grade_analysis_v2.mapper.StuMapper;
import com.keinn.grade_analysis_v2.model.QueryStuInfo;
import com.keinn.grade_analysis_v2.model.StuInfo;
import com.keinn.grade_analysis_v2.service.IStuManageService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StuManageService implements IStuManageService {


    private final StuMapper stuMapper;
    private final DateFormatConverter dateFormatConverter;


    /**
     * 查询学生
     *
     * @param stuInfo 查询条件
     * @return 学生列表
     */
    @Override
    public List<StuInfo> queryStudents(QueryStuInfo stuInfo) throws Exception {
        try {
            if (!StringUtils.isEmpty(stuInfo.getStartTimeStr())) {
                Timestamp startTime = null;
                startTime = dateFormatConverter.convertStringToTimestamp(stuInfo.getStartTimeStr());
                stuInfo.setStartTime(startTime);
            }
            if (!StringUtils.isEmpty(stuInfo.getEndTimeStr())) {
                Timestamp endTime = null;
                endTime = dateFormatConverter.convertStringToTimestamp(stuInfo.getEndTimeStr());
                stuInfo.setEndTime(endTime);
            }
        } catch (Exception e) {
            log.error("日期转换失败: {}", e.getMessage());
            throw new Exception("查询时间错误");
        }
        System.out.println(stuInfo);
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
    public boolean updateStudent(StuInfo stuInfo) throws Exception {
        StuInfo isExist = stuMapper.getStuInfoById(stuInfo.getStuInfoId());
        if (!Objects.isNull(isExist)) {
            throw new Exception("数据无修改");
        }
        return stuMapper.updateStuInfo(stuInfo);
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
    public boolean batchDeleteStudent(List<Long> stuInfoIds) {
        boolean b = stuMapper.deleteStuInfoById(stuInfoIds);
        return b;

    }
}
