package kevinlee.demo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class TestExtends {
    @Test
    public void test1(){
        List<Dog> list = Lists.newArrayList();
        for (String s : Arrays.asList("dog1", "dog2")) {
            Dog dog = new Dog();
            dog.setName(s);
            list.add(dog);
        }
        this.countLegs(list);
//        this.countLegs1(list); 报错


    }
    static int countLegs (List<? extends Animal > animals ) {
        int retVal = 0;
        for ( Animal animal : animals )
        {
             animal.getName();
        }
        return retVal;
    }

    static int countLegs1 (List< Animal > animals ){
        int retVal = 0;
        for ( Animal animal : animals )
        {
            animal.getName();
        }
        return retVal;
    }

    @Data
    class Dog extends Animal{
        private String name;
    }
    @Data
    class Animal{
        private Integer type;
        private String name;
    }
}
