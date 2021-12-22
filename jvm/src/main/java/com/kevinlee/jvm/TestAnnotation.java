package com.kevinlee.jvm;


import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * @ClassName TestAnnotation
 * @Author kevinlee
 * @Date 2021/12/22 18:40
 * @Version 1.0
 **/
public class TestAnnotation {
    public static void main(String[] args) {
        Annotation[] annotations = Student.class.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }

        TableLee annotation = Student.class.getAnnotation(TableLee.class);
        System.out.println(annotation.value());
        Field[] declaredFields = Student.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            FieldLee fieldLee = declaredField.getAnnotation(FieldLee.class);
            System.out.println(declaredField.getName()+"====="+fieldLee.columnName()+"========="+fieldLee.type());
        }
    }

}
@TableLee("db_test")
class Student{
    @FieldLee(columnName = "db_id",type = "Integer")
    private Integer id;
    @FieldLee(columnName = "db_age",type = "Integer")
    private Integer age;
    @FieldLee(columnName = "db_name",type = "String")
    private String name;
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface TableLee{
    String value();
}


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface FieldLee{
   String columnName();
   String type();
}
