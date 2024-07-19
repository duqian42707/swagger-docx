package com.dqv5.swagger.docx.pojo;

import lombok.Data;

import java.util.List;

@Data
public class RequestInfoVo {

    private String number;
    private String requestUrl;
    private String requestMethod;
    private String description;
    private List<RequestParamVo> parameters;
}