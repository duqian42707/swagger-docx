package com.dqv5.swagger.docx.web;

import com.dqv5.swagger.docx.service.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author duqian
 * @date 2024-07-19 周五
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private DocumentService documentService;

    @GetMapping("/test1")
    public String test1() throws IOException {
        documentService.wordExport();
        return "ok";
    }
}
