package com.dqv5.swagger.docx.web;

import com.dqv5.swagger.docx.pojo.DbdocConfigDTO;
import com.dqv5.swagger.docx.pojo.GenerateResult;
import com.dqv5.swagger.docx.service.DocumentService;
import com.dqv5.swagger.docx.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<InputStreamResource> downloadTemplates(HttpServletResponse response) throws IOException {
        String fileName = "default-template.docx";
        ClassPathResource classPathResource = new ClassPathResource("files/" + fileName);
        InputStream inputStream = classPathResource.getInputStream();
        InputStreamResource resource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
        headers.add("Content-Disposition", contentDisposition.toString());
        headers.add("Cache-Control", "no-cache,no-store,must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok().headers(headers).contentLength(inputStream.available())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @PostMapping("/generate")
    public void generate(@RequestParam MultipartFile[] swaggerFiles,
                         @RequestParam(required = false) MultipartFile templateFile,
                         @RequestParam String json, HttpServletResponse response) {
        DbdocConfigDTO param = JsonUtil.readValue(json, DbdocConfigDTO.class);
        GenerateResult result = documentService.generate(param, swaggerFiles, templateFile);
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
