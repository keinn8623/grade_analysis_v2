package com.keinn.grade_analysis_v2.common.Enum;

public enum OverAllData {
    STU_NAME("stuName","学生姓名"),
    TEXT_NAME("textName","考试名称"),
    TEA_NAME("teaName","老师姓名"),
    CAMPUS("campus","校区"),
    NUM_THEORY("numTheory","数论"),
    JOURNEY("journey","行程"),
    APPLICATION("application","应用"),
    COMBINATION("combination","组合"),
    GEOMETRY("geometry","几何"),
    COUNTER("counter","计数"),
    CALCULATION("calculation","计算");

    private final String code;
    private final String name;

    OverAllData(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
