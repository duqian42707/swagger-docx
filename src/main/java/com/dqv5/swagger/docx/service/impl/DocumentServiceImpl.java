package com.dqv5.swagger.docx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.dqv5.swagger.docx.pojo.*;
import com.dqv5.swagger.docx.service.DocumentService;
import com.dqv5.swagger.docx.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlgraphics.util.ClasspathResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
            List<RequestInfoVo> requestInfos = new ArrayList<>();
            for (MultipartFile swaggerFile : swaggerFiles) {
                InputStream inputStream = swaggerFile.getInputStream();
                String jsonStr = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                List<RequestInfoVo> requestInfoList = getRequestInfo(jsonStr);
                requestInfos.addAll(requestInfoList);
            }
            model.setRequestInfos(requestInfos);


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
            Configure configure = Configure.builder().bind("requestInfos", policy).bind("tables", policy)
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


    @Override
    public void wordExport() throws IOException {
        InputStream in = Files.newInputStream(Paths.get("C:\\Users\\duq\\Downloads\\logbook.json"));
        String jsonStr = IOUtils.toString(in, StandardCharsets.UTF_8);
        List<RequestInfoVo> requestInfoList = getRequestInfo(jsonStr);
        log.info("requestInfoList: {}", requestInfoList);
    }

    /**
     * 获取接口请求信息
     */
    private List<RequestInfoVo> getRequestInfo(String jsonStr) {
        JSONObject swaggerJson = JSON.parseObject(jsonStr, Feature.DisableCircularReferenceDetect);
        JSONObject paths = swaggerJson.getJSONObject("paths");
        Map<String, JSONObject> refMap = getDefinitions(swaggerJson);
        List<RequestInfoVo> requestList = new ArrayList<>();
        Set<String> pathSet = paths.keySet();
        int i = 0;
        for (String path : pathSet) {
            RequestInfoVo info = new RequestInfoVo();
            info.setNumber((i + 1) + "");
            i++;
            JSONObject infoJson = paths.getJSONObject(path);
            info.setRequestUrl(path);
            Set<String> methodSet = infoJson.keySet();
            for (String method : methodSet) {
                info.setRequestMethod(method);
                JSONObject paramInfo = infoJson.getJSONObject(method);
                String summary = paramInfo.getString("summary");
                info.setDescription(summary);
                JSONArray parameterArr = paramInfo.getJSONArray("parameters");
                List<RequestParamVo> parameters = new ArrayList<>();
                if (!CollectionUtils.isEmpty(parameterArr)) {
                    for (int j = 0; j < parameterArr.size(); j++) {
                        JSONObject paramJson = parameterArr.getJSONObject(j);
                        RequestParamVo param = JSON.parseObject(JSON.toJSONString(paramJson), RequestParamVo.class);
                        if (paramJson.containsKey("schema")) {
                            JSONObject schema = paramJson.getJSONObject("schema");
                            String ref = schema.getString("$ref");
                            if (StringUtils.isNotBlank(ref)) {
                                String def = ref.substring("#/definitions/".length());
                                JSONObject defJson = refMap.get(def);
                                param.setType(defJson.getString("type"));
                            }
                        }
                        param.setSerial((j + 1) + "");
                        parameters.add(param);
                    }
                }
                info.setParameters(parameters);
            }
            requestList.add(info);
        }
        return requestList;
    }


    private Map<String, JSONObject> getDefinitions(JSONObject swaggerJson) {
        Map<String, JSONObject> map = new HashMap<>();
        JSONObject definitions = swaggerJson.getJSONObject("definitions");
        Set<String> definitionSet = definitions.keySet();
        for (String def : definitionSet) {
            map.put(def, definitions.getJSONObject(def));
        }
        return map;
    }


}
