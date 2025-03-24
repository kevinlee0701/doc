package com.kevinlele.elasticsearch;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {
    
    static class Problem {
        String title;
        String content;
        String candidates;
        String answers;
    }

    public static void main(String[] args) throws Exception {
        List<Problem> problems = parseFile("/Users/kevinlee/work/idea-jituan/kevin-doc/elasticsearch/src/input.pdf");
        generateExcel(problems, "output.xlsx");
    }

    private static List<Problem> parseFile(String filename) throws IOException {
        List<Problem> problems = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        Problem currentProblem = null;
        boolean inTranslations = false;
        StringBuilder contentBuilder = new StringBuilder();

        Pattern titlePattern = Pattern.compile("^##\\s+\\d+\\.\\s+([^/]+?)\\s*/.*?#\\d+\\s+超高频");
        Pattern candidatePattern = Pattern.compile("候选词");
        Pattern answerPattern = Pattern.compile("答案词");
        Pattern translationPattern = Pattern.compile("逐句翻译");

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            Matcher titleMatcher = titlePattern.matcher(line);
            
            // 检测题目标题
            if (titleMatcher.find()) {
                if (currentProblem != null) {
                    currentProblem.content = processContent(contentBuilder.toString());
                    problems.add(currentProblem);
                    contentBuilder = new StringBuilder();
                }
                currentProblem = new Problem();
                String titleEn = titleMatcher.group(2).trim();
                String titleCn = titleMatcher.group(3).trim();
                currentProblem.title = titleMatcher.group(1) + ". " + titleEn + " / " + titleCn;
                continue;
            }

            // 处理候选词和答案词
            if (currentProblem != null) {
                if (candidatePattern.matcher(line).find()) {
                    currentProblem.candidates = reader.readLine().trim();
                } else if (answerPattern.matcher(line).find()) {
                    currentProblem.answers = reader.readLine().trim();
                } else if (translationPattern.matcher(line).find()) {
                    inTranslations = true;
                } else if (line.startsWith("## 题目解析")) { 
                    inTranslations = false;
                } else if (inTranslations) {
                    contentBuilder.append(line.replaceAll("\\s+\\d+\\s+", " ___ ")).append("\n");
                }
            }
        }

        if (currentProblem != null) {
            currentProblem.content = processContent(contentBuilder.toString());
            problems.add(currentProblem);
        }
        reader.close();
        return problems;
    }

    private static String processContent(String content) {
        // 移除解析部分并清理空行
        return content.replaceAll("(题目解析|Anne助教).*?(\\n|$)", "")
                     .replaceAll("\n+", "\n").trim();
    }

    private static void generateExcel(List<Problem> problems, String filename) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Problems");
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"题目标题", "题目内容", "候选词", "答案词"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填入数据
        int rowNum = 1;
        for (Problem p : problems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.title);
            row.createCell(1).setCellValue(p.content);
            row.createCell(2).setCellValue(p.candidates);
            row.createCell(3).setCellValue(p.answers);
        }

        // 调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            workbook.write(fos);
        }
        workbook.close();
    }
}
