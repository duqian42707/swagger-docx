package com.dqv5.swagger.docx.service.impl;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.dqv5.swagger.docx.pojo.DataModel;
import com.dqv5.swagger.docx.pojo.DbdocConfigDTO;
import com.dqv5.swagger.docx.pojo.GenerateResult;
import com.dqv5.swagger.docx.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlgraphics.util.ClasspathResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author duqian
 * @date 2021/6/2
 */
@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    public static final String FILE_SEPARATOR = File.separator;
    public static final String AUTO_DIR = System.getProperty("java.io.tmpdir") + FILE_SEPARATOR + "swagger-docx";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");


    @Override
    public GenerateResult generate(DbdocConfigDTO config, MultipartFile[] swaggerFiles, MultipartFile template) {
        try {
            log.info("config: {}", config);
            log.info("template: {}", template);
            String folderName = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            String folderPath = AUTO_DIR + FILE_SEPARATOR + folderName;
            File folder = new File(folderPath);
            if (!folder.mkdirs()) {
                throw new RuntimeException("创建临时目录失败:" + folderPath);
            }

            DataModel model = new DataModel();

            InputStream templateInputStream = null;
            if (template != null) {
                templateInputStream = template.getInputStream();
            } else {
                String fileName = "default-template.docx";
                ClassPathResource classPathResource = new ClassPathResource("files/" + fileName);
                templateInputStream = classPathResource.getInputStream();
            }

            //执行生成
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            Configure configure = Configure.builder().bind("columns", policy).bind("tables", policy)
                    .useSpringEL().build();
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateInputStream, configure);

            File file = new File(folderPath, "output.docx");
            try (OutputStream out = Files.newOutputStream(file.toPath())) {
                xwpfTemplate.render(model);
                xwpfTemplate.writeAndClose(out);
            }
            log.info("文档生成成功: {}", file);
            return new GenerateResult(folderName, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteTempFolder(String folderName) {
        String folderPath = AUTO_DIR + FILE_SEPARATOR + folderName;
        FileUtils.deleteQuietly(new File(folderPath));
    }
}
