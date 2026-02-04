package com.keinn.grade_analysis_v2.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseResult<T> {
    private int code;
    private String message;
    private T data;


    // 成功响应的静态方法
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "成功", data);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(200, message, data);
    }

    public static <T> ResponseResult<T> success(String message) {
        return new ResponseResult<>(200, message, null);
    }

    public static <T> ResponseResult<T> fail(String message) {
        return new ResponseResult<>(201, message, null);
    }



    // 失败响应的静态方法
    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<>(code, message, null);
    }
}

