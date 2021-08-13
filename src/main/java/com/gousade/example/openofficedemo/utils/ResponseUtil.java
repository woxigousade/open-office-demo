package com.gousade.example.openofficedemo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Objects;

@Slf4j
public class ResponseUtil {

    /**
     * 下载resource目录下的文件
     */
    public static void resourceFileDownload(HttpServletResponse response, String path, String filename) {
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (InputStream input = classPathResource.getInputStream(); OutputStream output = response.getOutputStream()) {
            /*InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/word.txt");
            InputStream inputStream2 =classPathResource.getInputStream();
            ClassPathResource classPathResource = new ClassPathResource("static/word.txt");
            //jar包不能getFile，只能通过流的形式读取
            File sourceFile =  classPathResource.getFile();
            File file = ResourceUtils.getFile("classpath:static/word.txt");
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(path);
            InputStream inputStream = resource.getInputStream();*/
            response.reset();
            String fileName = URLEncoder.encode(Objects.requireNonNull(filename), "UTF-8")
                    .replaceAll("\\+", "%20");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition",
                    "attachment;filename*=utf-8''" + fileName);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) != -1) {
                output.write(bytes, 0, length);
                output.flush();
            }
        } catch (Exception e) {
            log.error("下载文件时发生异常。", e);
        }
    }
}
