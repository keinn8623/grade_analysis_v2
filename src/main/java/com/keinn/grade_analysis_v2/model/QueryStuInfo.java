package com.keinn.grade_analysis_v2.model;

import lombok.Data;
import org.bouncycastle.util.Times;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class QueryStuInfo {
    private Long stuInfoId;

    private String stuName;

    private String teacher;

    private String grade;

    private String clazz;

    private String campus;

    private String phone;

    private String startTimeStr;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Timestamp startTime;

    private String endTimeStr;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Timestamp endTime;
}
