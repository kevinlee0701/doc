package com.kevin.juc;

import com.deepoove.poi.XWPFTemplate;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestWord
 * @Author kevinlee
 * @Date 2022/3/24 16:44
 * @Version 1.0
 **/
public class TestWord {

    @Test
    public void testWord() throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "lee");
        data.put("courseName", "服务名字");
        data.put("teacherName","李老师");
        String url= "/Users/kevinlee/idea-project-study/kevin-doc/juc/src/test/resources";
        XWPFTemplate template = XWPFTemplate.compile(url+"/test1.docx")
                .render(data);
        FileOutputStream out;
        out = new FileOutputStream(url+"/test2.docx");
        template.write(out);
        out.flush();
        out.close();
        template.close();
    }
}
