package com.dqv5.swagger.docx.web;

import cn.hutool.core.util.ZipUtil;
import com.dqv5.swagger.docx.pojo.GenerateParam;
import com.dqv5.swagger.docx.pojo.GenerateResult;
import com.dqv5.swagger.docx.service.DocumentService;
import com.dqv5.swagger.docx.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author duqian
 * @date 2023/9/11
 */
@RestController
@RequestMapping("/api/document")
public class DocumentController {
    @Resource
    private DocumentService documentService;

    @GetMapping("/download-templates")
    public void downloadTemplates(HttpServletResponse response) throws IOException {
        String[] templateFileNames = {
                "template1.docx",
                "template2.docx",
                "template3.docx",
        };
        InputStream[] ins = new InputStream[templateFileNames.length];
        for (int i = 0; i < templateFileNames.length; i++) {
            String templateFileName = templateFileNames[i];
            ClassPathResource classPathResource = new ClassPathResource("files/" + templateFileName);
            InputStream inputStream = classPathResource.getInputStream();
            ins[i] = inputStream;
        }
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("templates.zip", StandardCharsets.UTF_8)
                .build();
        response.setHeader("Content-Disposition", contentDisposition.toString());
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        ZipUtil.zip(response.getOutputStream(), templateFileNames, ins);
    }

    @PostMapping("/generate")
    public void generate(@RequestParam String swaggerJson,
                         @RequestParam MultipartFile templateFile,
                         @RequestParam String paramJson, HttpServletResponse response) {
        GenerateResult result = documentService.generate(swaggerJson, templateFile, paramJson);
        String folderName = result.getFolderName();
        File file = result.getFile();

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = Files.newInputStream(file.toPath());
            outputStream = response.getOutputStream();

            String filename = URLEncoder.encode(file.getName(), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=%s", filename));
            response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setContentLength(inputStream.available());
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
            documentService.deleteTempFolder(folderName);
        }
    }
}
