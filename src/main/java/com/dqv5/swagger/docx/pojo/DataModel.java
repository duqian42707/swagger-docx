package com.dqv5.swagger.docx.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author duqian
 * @date 2024-07-19 周五
 */
@Data
public class DataModel {
    // 全部接口
    private List<RequestInfoVo> requestInfos = new ArrayList<>();
    // 按模块分组
    private List<ModuleInfoVo> modules = new ArrayList<>();
}
