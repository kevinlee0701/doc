package com.kevinlee.elasticsearch.excel;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
 
public class ReadAndWriteExcel {
 
    /**
     * 读excel
     * 处理指定列
     * 写入新的excel
     * @throws IOException
     */
    public static void readE() throws IOException {
        //写-创建Excel文件薄
        HSSFWorkbook writeWorkbook = new HSSFWorkbook();
        //写-创建工作表sheeet
        HSSFSheet writeSheet = writeWorkbook.createSheet();
        //写入-创建第一行
        HSSFRow row = writeSheet.createRow(0);
        String[] title = {"身份证加密","手机号加密"};
        HSSFCell cell = null;
        for (int j = 0; j < title.length; j++) {
            cell = row.createCell(j);
            cell.setCellValue(title[j]);
        }
        //读-文件
        File readFile=new File("D:/要读的文件.xls");
        HSSFWorkbook readWorkbook=new HSSFWorkbook(FileUtils.openInputStream(readFile));
        HSSFSheet readSheet=readWorkbook.getSheetAt(0);
        int lastRowNum=readSheet.getLastRowNum();
        for (int i=1;i<=lastRowNum;i++){
            HSSFRow readRow=readSheet.getRow(i);
            //获取当前行最后单元格列号
            //int lastCellNum=readRow.getLastCellNum();
            //读 -指定的几列--我这里需要2 3列 
            HSSFCell r1=readRow.getCell(2);
            HSSFCell r2=readRow.getCell(3);
             
            //读出来的数据 进行MD5加密
            //根据自己的需要处理相关的数据
            String value=r1.getStringCellValue();
            String value2=r2.getStringCellValue();
            //String zj = Md5Utils.getStringMD5(value);
            //String sj = Md5Utils.getStringMD5(value2);
 
            //写入文件
            HSSFRow nextrow = writeSheet.createRow(i);
            HSSFCell w1 = nextrow.createCell(0);
            w1.setCellValue("zj");
            HSSFCell w2 = nextrow.createCell(1);
            w2.setCellValue("sj");
        }
        //写-创建一个文件
        File file = new File("D:/要写入的文件.xls");
        file.createNewFile();
        FileOutputStream stream = FileUtils.openOutputStream(file);
        writeWorkbook.write(stream);
        stream.close();
 
    }
}