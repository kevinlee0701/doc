package com.kevin.juc;

import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 通用测试类
 * @ClassName TestWord
 * @author kevinlee
 * @date 2022/3/24 16:44
 * @Version 1.0
 **/
@Slf4j
public class TestWord {
    /**
     *
     * 测试word插件
     * @throws IOException
     */
    @Test
    public void testWord() throws IOException {
        log.info("===info");
        log.warn("===warn");
        log.error("===error");
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

    /**
     * flatMap 与 map
     */
    @Test
    public void testFlatMap() {
      List<KlassGroup> list = Arrays.asList(
              new KlassGroup(new Klass(1), new Klass(2), new Klass(3)),
              new KlassGroup(new Klass(4), new Klass(5), new Klass(6)),
              new KlassGroup(new Klass(7), new Klass(8), new Klass(9)),
              new KlassGroup(new Klass(10))
      );
        List<Klass> collect1 = list.stream().
                flatMap(s -> s.getKlassList().stream())
                .filter(s -> s.getField()!=1)
                .collect(Collectors.toList());
        List<Stream<Klass>> collect = list.stream().map(s -> s.getKlassList().stream()).collect(Collectors.toList());

        list.stream().flatMap(s -> s.getKlassList().stream()).collect(Collectors.toList()).forEach(System.out::println);
//
        List<Integer> list2 = new ArrayList<>();
        list2.add(1);
        list2.add(2);

        List<String> collect4 = list2.stream().map(String::valueOf).collect(Collectors.toList());
    }

    private static class Klass {
        private int field;
        public Klass(int field) {
            this.field = field;
        }

        public int getField() {
            return field;
        }

        @Override
        public String toString() {
            return "field=" + field;
        }
    }
    private static class KlassGroup {
        private List<Klass> group = new ArrayList<>();
        public KlassGroup(Klass... objList) {
            for (Klass item : objList) {
                this.group.add(item);
            }
        }
        public List<Klass> getKlassList() {
            return group;
        }
    }

    public enum Status {
        FREE, BUSY, VOCATION;
    }

    /**
     * 查找 / 匹配
     */
    @Test
    public void testMatch(){
        List<Status> list = Arrays.asList(Status.FREE, Status.BUSY, Status.VOCATION);
        //allMatch：检查是否匹配所有元素
        boolean flag1 = list.stream()
                .allMatch((s) -> s.equals(Status.BUSY));
        System.out.println(flag1);
        //anyMatch：检查是否至少匹配一个元素
        boolean flag2 = list.stream()
                .anyMatch((s) -> s.equals(Status.BUSY));
        System.out.println(flag2);
        //noneMatch：检查是否没有匹配所有元素
        boolean flag3 = list.stream()
                .noneMatch((s) -> s.equals(Status.BUSY));
        System.out.println(flag3);

        // 避免空指针异常 findFirst：返回第一个元素
        Optional<Status> op1 = list.stream()
                .findFirst();
        // 如果Optional为空 找一个替代的对象
        Status s1 = op1.orElse(Status.BUSY);
        System.out.println(s1);
        //findAny：返回当前流中的任意元素
        Optional<Status> op2 = list.stream()
                .findAny();
        System.out.println(op2);

        long count = list.stream()
                .count();
        System.out.println(count);
    }

    /**
     * reduce
     */
    @Test
    public void testReduce(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Integer integer = list.stream()
                .reduce(0, BinaryOperator.maxBy(Integer::compareTo));
        System.out.println(integer);
    }

    @Test
    public void ofNullable(){
        String tt = null;
        String s = Optional.ofNullable(tt).orElse("12345");
        System.out.println("返回结果："+s);
    }

























}
