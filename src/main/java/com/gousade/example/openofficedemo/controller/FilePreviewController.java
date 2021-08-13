package com.gousade.example.openofficedemo.controller;

import com.gousade.example.openofficedemo.utils.OpenOfficeUtil;
import com.gousade.example.openofficedemo.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.DocumentConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author woxigousade
 * @date 2019/7/22
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/gousadeTest/filePreview")
public class FilePreviewController {

    @Autowired
    private DocumentConverter converter;

    @Autowired
    private HttpServletResponse response;


    /**
     * PDF预览
     */
    @GetMapping("/previewPDF")
    public void previewPDF() {
        String path = "static" + File.separator + "pdf" + File.separator + "Java并发编程的艺术.pdf";
        String filename = "Java并发编程的艺术.pdf";
        ClassPathResource classPathResource = new ClassPathResource(path);
        response.reset();
        try (InputStream input = classPathResource.getInputStream(); OutputStream output = response.getOutputStream()) {
//            String fileName = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
//            FileInputStream input = new FileInputStream("E:\\document\\PENET协议_R_2015.pdf");
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) != -1) {
                output.write(bytes, 0, length);
                output.flush();
            }
            /*response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline;filename*=utf-8''" + fileName);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文档预览，word、excel、ppt都可以转换为pdf，excel样式可能会出现问题
     */
    @GetMapping("/previewDocument")
    public void previewDocument() {
        String path = "static" + File.separator + "pdf" + File.separator + "测试word1.docx";
        String filename = "测试word.pdf";
        File convertDictionary = new File("D:/convertToPDF");
        if (!convertDictionary.exists()) {
            convertDictionary.mkdirs();
        }
        File convertedFile = new File(convertDictionary.getAbsolutePath() + File.separator + filename);
        ClassPathResource classPathResource = new ClassPathResource(path);
        response.reset();
        try (OutputStream output = response.getOutputStream()) {
            converter.convert(classPathResource.getInputStream()).as(DefaultDocumentFormatRegistry.DOCX)
                    .to(convertedFile).execute();
            InputStream input = new FileInputStream(convertedFile);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) != -1) {
                output.write(bytes, 0, length);
                output.flush();
            }
            input.close();
            //需要保存文件用上面的方法，如果不需要保存文件直接预览则直接输出为PDF流，输入的as类型似乎没影响
            /*converter.convert(classPathResource.getInputStream()).as(DefaultDocumentFormatRegistry.PDF)
                    .to(output).as(DefaultDocumentFormatRegistry.PDF)
                    .execute();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 只使用local包下的类进行文档转换预览
     */
    @GetMapping("/previewDocumentByLocal")
    private void previewDocumentByLocal() {
        String path = "static" + File.separator + "pdf" + File.separator + "防汛保畅日报template.docx";
        String filename = "测试wordLocal.pdf";
        File convertDictionary = new File("D:/convertToPDFLocal");//转换之后文件生成的地址
        if (!convertDictionary.exists()) {
            convertDictionary.mkdirs();
        }
        File convertedFile = new File(convertDictionary.getAbsolutePath() + File.separator + filename);
        ClassPathResource classPathResource = new ClassPathResource(path);
        response.reset();
        try (OutputStream output = response.getOutputStream()) {
            OpenOfficeUtil.getInstance().convert(classPathResource.getInputStream(), convertedFile);
            InputStream input = new FileInputStream(convertedFile);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) != -1) {
                output.write(bytes, 0, length);
                output.flush();
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片预览
     */
    @GetMapping("/previewImage")
    public void picPreview() {
        response.reset();
        response.setContentType("text/html; charset=UTF-8");
        response.setContentType("image/jpeg");
        String path = "static" + File.separator + "sliderImages" + File.separator + "background"
                + File.separator + "background (1).jpg";
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (InputStream input = classPathResource.getInputStream();
             OutputStream output = response.getOutputStream()) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) != -1) {
                output.write(bytes, 0, length);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/fileDownload")
    public void fileDownload(String filename) {
        String path = "static" + File.separator + filename;
        ResponseUtil.resourceFileDownload(response, path, filename.substring(filename.lastIndexOf("/") + 1));
    }

    @GetMapping("/fileDownload2")
    public void fileDownload2() {
        try {
            Resource resource = new ClassPathResource("application.properties");
            System.out.println(resource.getURL());
            File sourceFile = new File(String.valueOf(resource.getURL()));
            System.out.println(sourceFile.getAbsolutePath());
            File backgroundDictionary = new File(sourceFile.getParent() + "/pdf");
            System.out.println("是否是目录：" + backgroundDictionary.isDirectory());
            File[] files = backgroundDictionary.listFiles();
            System.out.println("目录下文件数：" + (files != null ? files.length : 0));
            System.out.println(sourceFile.getAbsolutePath());
            try (InputStream input = new FileInputStream(sourceFile); OutputStream output = response.getOutputStream()) {
                response.reset();
                String fileName = URLEncoder.encode("application.properties", "UTF-8")
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
