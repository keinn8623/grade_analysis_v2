package com.keinn.grade_analysis_v2.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class StuTextInfo extends BaseModel{
    private Long stuTextInfoId;

    private String stuName;

    private String textName;

    private String teaName;

    private String campus;

    private List<Integer> pointsLostNo;

    private List<Integer> pointsGetNo;
}
