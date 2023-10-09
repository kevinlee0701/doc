package com.kevinlee.web.demos.web;

import com.kevinlee.web.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author lqd
 */
@Controller
@RequestMapping("file")
@CrossOrigin(origins = "*")
@Slf4j
public class IoFileController {

    @Value("${file.path}")
    private String path;

    @PostMapping("upload")
    public String uploadFile(@RequestParam("files") MultipartFile[] files) {
        FileUtils.saveFile(files, path);
        return "文件上传成功！";
    }

    @GetMapping(value = "/download", consumes = MediaType.ALL_VALUE)
    public void downloadFile(HttpServletResponse response, String fileName) throws IOException {
        FileUtils.getInputStream(response, path + "/" + fileName);
    }

}