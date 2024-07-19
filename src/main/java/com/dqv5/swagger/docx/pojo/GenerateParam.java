package com.dqv5.swagger.docx.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author duqian
 * @date 2021/6/2
 */
@Data
public class GenerateParam {

    private List<Module> moduleList = new ArrayList<>();


    @Data
    public static class Module {
        private String name;
        private String description;
        private List<String> apiIds;
    }
}
