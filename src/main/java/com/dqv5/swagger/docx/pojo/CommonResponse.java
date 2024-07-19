package com.dqv5.swagger.docx.pojo;

import lombok.Data;

/**
 * @author duq
 * @date 2024/6/7
 */
@Data
public class CommonResponse<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> CommonResponse<T> build(boolean success, String msg) {
        return build(success, msg, null);
    }

    public static <T> CommonResponse<T> build(boolean success, String msg, T data) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setCode(success ? 200 : 500);
        commonResponse.setMsg(msg);
        commonResponse.setData(data);
        return commonResponse;
    }
}
