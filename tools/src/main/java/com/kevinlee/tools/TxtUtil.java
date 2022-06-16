package com.kevinlee.tools;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TxtUtil
 * @Author kevinlee
 * @Date 2022/6/16 15:19
 * @Version 1.0
 **/
public class TxtUtil {
    public static void main(String[] args) throws IOException {

        String path = TxtUtil.class.getResource("/test.txt").getPath();
        String path2 = TxtUtil.class.getResource("/").getPath();
        System.out.println(path);
        List<String> readTxtFile = readTxtFile(path, read -> {
            System.out.println(read);
            return read;
        });
       writeTxtFile(path2,readTxtFile);
    }

    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath 文件路径
     * @param  handle  获取文件后处理类
     */
    public static List<String > readTxtFile(String filePath,TxthandleInterface handle){
        List result =  new ArrayList<String>();
        try {
            String encoding="UTF8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    String s = handle.doSomething(lineTxt);
                    result.add(s);
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return result;
    }

    public static void writeTxtFile(String filePath,List<String> data) throws IOException {
        File file = new File(filePath + "/result.txt");
        //if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.writeLines(file,"UTF-8",data,true);
    }

}
