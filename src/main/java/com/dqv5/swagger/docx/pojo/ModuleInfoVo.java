package com.dqv5.swagger.docx.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author duqian
 * @date 2024-07-19 周五
 */
@Data
public class ModuleInfoVo {
    private String name;
    private String description;
    private List<RequestInfoVo> requestInfos;
}
