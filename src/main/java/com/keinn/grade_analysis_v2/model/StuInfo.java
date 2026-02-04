package com.keinn.grade_analysis_v2.model;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class StuInfo extends BaseModel{
    private Long stuInfoId;

    private String stuName;

    private String teacher;

    private String grade;

    private String clazz;

    private String campus;

    private String phone;


}
