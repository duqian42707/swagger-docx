package com.dqv5.swagger.docx.service;

import com.dqv5.swagger.docx.pojo.GenerateParam;
import com.dqv5.swagger.docx.pojo.GenerateResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author duqian
 * @date 2020/7/22
 */

public interface DocumentService {
    /**
     * 生成文件并下载
     */
    GenerateResult generate(String swaggerJson, MultipartFile templateFile, String paramJson);

    void deleteTempFolder(String folderName);

    void wordExport() throws IOException;
}
