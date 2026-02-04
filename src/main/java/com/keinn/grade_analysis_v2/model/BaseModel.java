package com.keinn.grade_analysis_v2.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseModel {
    private int deleted;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String createUser;

    private String updateUser;
}
